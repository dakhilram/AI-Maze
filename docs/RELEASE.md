# Android Release Guide

AI Maze is configured as a free, offline Android application.

## Current package

```text
com.akhil.aimaze
```

Current version:

```text
versionCode = 1
versionName = 1.0
```

Increase `versionCode` for every Google Play upload. Change `versionName` when the user-visible version changes.

## Pre-release verification

From the project root:

### Windows

```powershell
.\gradlew.bat clean test assembleDebug
.\gradlew.bat lint
```

Install and manually verify the debug build on at least one emulator or Android device:

- Home navigation
- Play Maze at 8×8, 12×12, 16×16, and 20×20
- seeded maze reproduction
- manual movement and blocked-wall behavior
- Q-Learning training
- learned-path visualization
- A*/Dijkstra/Random benchmark
- benchmark history persistence
- history deletion
- light and dark mode
- rotation/resizing
- Android system back navigation

## Signing

Do not commit signing files to Git.

Create the upload key locally in Android Studio:

```text
Build > Generate Signed App Bundle or APK
```

Choose **Android App Bundle**, create/select your upload keystore, and keep the keystore password and key password private.

The repository ignores:

- `*.jks`
- `*.keystore`
- `keystore.properties`

For the first Google Play release, prefer **Play App Signing** so Google protects the app-signing key while you retain the upload key.

## Release bundle

In Android Studio:

```text
Build > Generate Signed App Bundle or APK > Android App Bundle
```

Choose the `release` variant.

The resulting bundle is normally under:

```text
app/build/outputs/bundle/release/
```

## Privacy/release facts

- no INTERNET permission
- no runtime permissions
- no advertising
- no analytics
- no account creation
- no paid APIs
- no remote backend
- benchmark history stored locally
- Android cloud backup disabled

See [Privacy and Security](PRIVACY_SECURITY.md).

## Before version 1.0 production

Add final screenshots/GIFs after device verification, confirm the launcher icon/branding visually, run the signed release build on a physical device, and complete Google Play's current testing and policy requirements.
