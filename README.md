# Liquid Glass Music

A native Android music player with Apple Music-inspired liquid glass UI and Spotify integration.

[![Build APK](https://github.com/Maxye4655/liquid-glass-music/actions/workflows/build.yml/badge.svg)](https://github.com/Maxye4655/liquid-glass-music/actions/workflows/build.yml)
![Platform](https://img.shields.io/badge/Platform-Android-3DDC84?style=flat-square&logo=android)
![License](https://img.shields.io/badge/License-MIT-yellow?style=flat-square)

## Download & Install

1. **Download the APK** from [Releases](https://github.com/Maxye4655/liquid-glass-music/releases/latest) or from the latest [GitHub Actions build](https://github.com/Maxye4655/liquid-glass-music/actions/workflows/build.yml) (click the most recent run, then download the `LiquidGlassMusic-debug` artifact)
2. **Install** the APK on your Android device (enable "Install from unknown sources" if prompted)
3. **Open the app** and tap **Connect with Spotify** -- login happens entirely inside the app

That's it.

## Features

- **Liquid Glass UI** -- Real optical refraction, blur, and vibrancy effects using the [Backdrop](https://github.com/Kyant0/AndroidLiquidGlass) library
- **Spotify Login In-App** -- OAuth login via built-in WebView, no browser switching
- **Search & Browse** -- Search songs, albums, artists. Browse genre categories
- **Full Player** -- Play/pause, skip, shuffle, repeat, seek, album art with dynamic color extraction
- **Offline Downloads** -- Download tracks for offline listening
- **Library** -- Your playlists, albums, and downloaded music in one place

## How It Works

1. Install the APK
2. Open the app and tap "Connect with Spotify"
3. Login with your Spotify account (free or premium)
4. Browse, search, and play music
5. Download songs for offline playback

## Liquid Glass

The UI uses Apple's liquid glass design language via the [Backdrop](https://github.com/Kyant0/AndroidLiquidGlass) library:

- **Blur** -- Gaussian blur creates frosted glass
- **Refraction** -- AGSL shaders bend background content near edges (Android 13+)
- **Vibrancy** -- Saturation boost for vivid colors through glass
- **Highlights** -- Specular edge glow simulates curved glass
- **Press effects** -- Glass clears on tap with scale + refraction animation

On older Android versions, the effects degrade gracefully to translucent surfaces with shadows.

## Tech Stack

Kotlin 2.1 / Jetpack Compose / Material3 / Backdrop (liquid glass) / Hilt / Room / Retrofit / Coil / Media3 / Spotify Web API

## License

MIT
