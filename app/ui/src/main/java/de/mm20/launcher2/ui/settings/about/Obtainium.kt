package de.mm20.launcher2.ui.settings.about

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.net.toUri
import de.mm20.launcher2.ktx.tryStartActivity

/**
 * Obtainium deep links, see https://wiki.obtainium.imranr.dev/deep_links/
 *
 * Obtainium keeps updates for apps it tracks, so the point of these is to let the user add
 * Kvaesitso to Obtainium in one tap instead of pasting a URL into the Add screen by hand.
 */
internal object Obtainium {

    /**
     * The GitHub repository. Obtainium's GitHub source detects this host and resolves it without
     * needing a source override.
     */
    private const val REPO_URL = "https://github.com/MM2-0/Kvaesitso"

    private const val AUTHOR = "MM2-0"

    private const val NAME = "Kvaesitso"

    /**
     * Opens Obtainium's Add App screen, pre-filled with Kvaesitso.
     *
     * [packageName] is used as the config id, which should be the package name of the currently
     * running build, so that debug/nightly builds are tracked separately from release builds
     * instead of overwriting each other.
     *
     * Falls back to Obtainium's website if Obtainium is not installed, because a bare
     * `obtainium://` link does nothing at all when nothing is there to handle it.
     *
     * @return whether Obtainium (or the website) was opened
     */
    fun addKvaesitso(context: Context, packageName: String): Boolean {
        val config = """{"id":"$packageName","url":"$REPO_URL","author":"$AUTHOR","name":"$NAME"}"""

        val obtained = context.tryStartActivity(
            Intent(Intent.ACTION_VIEW).apply {
                data = "obtainium://app/${Uri.encode(config)}".toUri()
            }
        )
        if (obtained) return true

        // The redirect page attempts the app link itself, then offers to install Obtainium.
        return context.tryStartActivity(
            Intent(Intent.ACTION_VIEW).apply {
                data = "https://apps.obtainium.imranr.dev/redirect?r=obtainium://app/${Uri.encode(config)}"
                    .toUri()
            }
        )
    }
}
