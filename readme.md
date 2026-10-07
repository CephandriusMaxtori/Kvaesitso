# Kvaesitso

<img src="https://raw.githubusercontent.com/MM2-0/Kvaesitso/main/assets/icons/ic_launcher.png" width="128">

Kvaesitso is a search focused, free and open source launcher for Android.

[Website and documentation](https://kvaesitso.mm20.de)

## Installation

### Using an F-Droid client and MM20's repo

The preferred way of installation is using the [F-Droid](https://f-droid.org) application. That way
you will always be notified about updates. Kvaesitso is available in the official F-Droid
repository, but all features depending on non-foss external APIs were removed.
For feature-complete builds you can add [MM20's repository](https://fdroid.mm20.de). Just scan the code below or open
the link on your phone:

<img src="https://fdroid.mm20.de/repo/index.png" width="150" alt="QR code">

https://fdroid.mm20.de/repo/

The same version is also available in [IzzyOnDroid's repository](https://apt.izzysoft.de/fdroid/index/apk/de.mm20.launcher2.release).

### Using Obtainium

[Obtainium](https://obtanium.imranr.dev) installs and updates apps straight from their release pages.
You can add Kvaesitso in one tap from **Settings > About > Get updates with Obtainium**, which opens
Obtainium's Add App screen pre-filled with this app. Alternatively, add
`https://github.com/MM2-0/Kvaesitso` manually; Obtainium detects GitHub on its own and will not ask
you to pick a source.

<a href="obtainium://app/%7B%22id%22%3A%22de.mm20.launcher2.release%22%2C%22url%22%3A%22https%3A%2F%2Fgithub.com%2FMM2-0%2FKvaesitso%22%2C%22author%22%3A%22MM2-0%22%2C%22name%22%3A%22Kvaesitso%22%7D">
  <img src="https://apps.obtainium.imranr.dev/redirect?r=obtainium://app/%7B%22id%22%3A%22de.mm20.launcher2.release%22%2C%22url%22%3A%22https%3A%2F%2Fgithub.com%2FMM2-0%2FKvaesitso%22%2C%22author%22%3A%22MM2-0%22%2C%22name%22%3A%22Kvaesitso%22%7D" alt="Get it on Obtainium" width="161" height="48">
</a>

> [!NOTE]
> Obtainium tracks the GitHub version of the launcher, which is signed differently from the official
> F-Droid builds. Installing one over the other will fail; see
> [Launcher Cannot Be Updated](https://kvaesitso.mm20.de/docs/user-guide/troubleshooting/update-not-installed).

### Manual installation

You can also download the latest release from
the [releases page](https://github.com/MM2-0/Kvaesitso/releases/latest) and install it manually.

## Report issues

If you notice any bugs or issues create a new issue in
the [issue tracker](https://github.com/MM2-0/Kvaesitso/issues). Before you do, please search the
existing issues for any similar issues. Please include any relevant information such as steps to
reproduce, stack traces, logs, and device information. These information can be founder under
Settings > Debug > Crash reporter and Settings > Debug > Export debug information.

## Feature requests

If you have an idea for a new feature, just create a new issue. However, there is no guarantee that
they will be implemented. If it's important for you, consider implementing it yourself,
see [contribute](#contribute).


## Contribute

Contributions are always welcome. If you want to fix any existing issues or implement smaller new
features just create a new pull request. If you plan to implement any (bigger) new features, please
create an issue first so we can discuss if and how this feature should be implemented.

If you want to help translating, see [how to translate the project.](https://kvaesitso.mm20.de/docs/contributor-guide/i18n)

<a href="https://i18n.mm20.de/engage/kvaesitso/">
<img src="https://i18n.mm20.de/widgets/kvaesitso/-/287x66-grey.png" alt ="Translation Status">
</a>

## Links

- User guide: https://kvaesitso.mm20.de/docs/user-guide
- F-Droid-Repository: https://fdroid.mm20.de

## Thanks to

- [@EliotAku](https://github.com/EliotAku) for the app icon
- All [translators and code contributors](https://github.com/MM2-0/Kvaesitso/graphs/contributors)

## License

This software is free software licensed under the GNU General Public License 3.0.

```

Copyright (C) 2021–2026 MM2-0 and the Kvaesitso contributors

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <https://www.gnu.org/licenses/>.
```

The plugin SDK modules (`plugins/sdk` and `core/shared`) are licensed under the Apache License 2.0.
