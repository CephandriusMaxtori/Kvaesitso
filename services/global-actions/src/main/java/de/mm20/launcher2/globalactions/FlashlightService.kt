package de.mm20.launcher2.globalactions

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager
import androidx.core.content.ContextCompat
import androidx.core.content.getSystemService
import de.mm20.launcher2.crashreporter.CrashReporter
import de.mm20.launcher2.ktx.checkPermission
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Controls the camera flash, which is used as a flashlight.
 *
 * All functions in this class are no-ops if the device has no camera flash, or if the camera
 * permission has not been granted.
 */
class FlashlightService(private val context: Context) {

    private val cameraManager: CameraManager?
        get() = context.getSystemService()

    /**
     * Whether this device has a camera flash, which can be used as a flashlight.
     */
    val hasFlashlight: Boolean =
        context.packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)

    private val _enabled = MutableStateFlow(false)

    /**
     * Whether the flashlight is currently turned on.
     */
    val enabled: StateFlow<Boolean> = _enabled.asStateFlow()

    private var listening = false

    private var flashCameraId: String? = null

    private val torchCallback = object : CameraManager.TorchCallback() {
        override fun onTorchModeChanged(cameraId: String, enabled: Boolean) {
            if (cameraId == flashCameraId) {
                _enabled.value = enabled
            }
        }

        override fun onTorchModeUnavailable(cameraId: String) {
            if (cameraId == flashCameraId) {
                _enabled.value = false
            }
        }
    }

    init {
        if (hasFlashlight) {
            startListening()
        }
    }

    /**
     * Turns the flashlight on if it is currently off, and vice versa.
     */
    fun toggle() {
        setEnabled(!_enabled.value)
    }

    /**
     * Turns the flashlight on or off.
     */
    fun setEnabled(enabled: Boolean) {
        val cameraManager = cameraManager ?: return
        val cameraId = findFlashCameraId() ?: return
        try {
            cameraManager.setTorchMode(cameraId, enabled)
            _enabled.value = enabled
        } catch (e: Exception) {
            CrashReporter.logException(e)
        }
    }

    /**
     * Starts listening for torch mode changes, so that [enabled] stays up to date even if the
     * flashlight is toggled by something else.
     *
     * Does nothing if already listening, if the device has no flashlight, or if the camera
     * permission has not been granted. Because of the latter, this needs to be called again
     * after the permission has been granted.
     */
    fun startListening() {
        val cameraManager = cameraManager ?: return
        if (listening || !hasFlashlight) return
        val cameraId = findFlashCameraId() ?: return
        try {
            cameraManager.registerTorchCallback(
                ContextCompat.getMainExecutor(context),
                torchCallback,
            )
            flashCameraId = cameraId
            listening = true
        } catch (e: Exception) {
            CrashReporter.logException(e)
        }
    }

    private fun findFlashCameraId(): String? {
        flashCameraId?.let { return it }
        if (!context.checkPermission(Manifest.permission.CAMERA)) return null
        val cameraManager = cameraManager ?: return null
        return try {
            cameraManager.cameraIdList.firstOrNull { id ->
                cameraManager.getCameraCharacteristics(id)
                    .get(CameraCharacteristics.FLASH_INFO_AVAILABLE) == true
            }
        } catch (e: Exception) {
            CrashReporter.logException(e)
            null
        }
    }
}
