# AetherStream APK Build Distribution

This folder contains the compiled Android Application Package (APK) for **AetherStream**.

## Package Details

- **Application Name**: AetherStream
- **Package ID**: `com.example`
- **Version**: 1.0.0 (`versionCode: 1`)
- **Min Android SDK**: Android 7.0 (API level 24)
- **Target Android SDK**: Android 16 (API level 36)
- **Architecture**: Universal (`arm64-v8a`, `armeabi-v7a`, `x86_64`)
- **File Name**: `AetherStream-v1.0.0.apk` / `app-debug.apk`
- **File Size**: ~23 MB
- **SHA-256 Checksum**: `089de43f41ee17a4dce46183e8b8ec7691b4cb84ed69299363a37acc8ae8579a`

## How to Install / Sideload on Android

1. Transfer `AetherStream-v1.0.0.apk` to your Android device or download it from the file explorer.
2. Tap the `.apk` file in your Android File Manager.
3. If prompted, toggle **"Allow installation from this source"** in device Settings.
4. Tap **Install** to complete installation and launch AetherStream.

## Rebuilding the APK

To generate a fresh APK from source:
```bash
gradle assembleDebug
```
The output will be placed in `app/build/outputs/apk/debug/app-debug.apk`.
