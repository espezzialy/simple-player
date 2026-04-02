# Simple Player

A Kotlin Android sample app that browses music metadata from the **iTunes Search API**, plays a **UI-only “mock” player** (no actual audio decoding), and adapts layout for **phone** and **tablet** form factors.

---

## Demo videos 

### Tablet

https://github.com/user-attachments/assets/047fcd73-2ed0-4cb6-887f-d6282e2b0cd4

_Tablet demo: With swipe side bar notification and playlist_


### Phone


https://github.com/user-attachments/assets/b7c99d0d-60aa-45c3-9190-969e67b30572



_Phone demo: With Swipe and notification_

---

## Table of contents

- [Overview](#overview)
- [Features](#features)
- [Architecture](#architecture)
- [Tech stack & libraries](#tech-stack--libraries)
- [Key decisions](#key-decisions)
- [CI & GitHub Actions](#ci--github-actions)
- [Reference documentation](#reference-documentation)
- [Project commands](#project-commands)
- [Compatibility & possibilities](#compatibility--possibilities)

---

## Overview

**Simple Player** demonstrates:

- **Jetpack Compose** UI with **Material 3**, responsive layouts, and navigation transitions.
- **Clean-ish layering**: `domain` (models, repository contracts, use cases), `data` (Retrofit, mappers, repositories, DataStore, session state), `presentation` (ViewModels, screens), `core` (helpers), `media` (Media3 notification / session integration).
- **Dependency injection** with **Hilt** (singletons, ViewModels, `@ApplicationContext`).
- **Apple iTunes Search API** for track/album metadata and artwork URLs (no proprietary streaming).

The app is **not** a full music streaming client: playback is simulated in the UI (progress, labels, play/pause) while **Media3** drives a **media-style notification** and **MediaSession** for system integration (controls, tap-to-return).

---

## Features

### Songs

- Search the iTunes catalog by keyword.
- Paginated / “load more” behaviour (with in-memory paging nuances documented in code—see `SongsSearchRepository`).
- Pull-to-refresh style affordances where implemented.
- Navigation to **album detail** and to the **player** with encoded route arguments.

### Album detail

- Loads collection metadata and track listing via repository / use case.
- Tablet- and phone-specific layouts (hero, lists, top bars).

### Player (mock playback)

- **Seek bar**, time labels, **previous / next** in queue (domain logic in `PlayerQueueNavigation`).
- **Repeat playlist** toggle (where exposed in UI state).
- **Artwork gestures (phone & tablet):** horizontal **swipe** on the cover art — swipe **left** for **next** track, swipe **right** for **previous** (threshold in `SimplePlayerDimens.Player.artworkSwipeHorizontalThreshold`; RTL-aware).
- **Side panel** with context-dependent content:
  - Search results when coming from search.
  - Album tracks when opened from an album.
  - **Recent songs** (persisted locally).
- **Lottie** / artwork integration patterns as used in the project.

### Recent songs & offline-friendly cache

- **DataStore Preferences** + JSON codec (`RecentSongsRepository`, `RecentSongsJsonCodec`) persist a **local list of recently played tracks** on device.
- That list stays available **without network**: you can still open the player from **recent songs** when search/API calls are unavailable (e.g. no connectivity), as long as entries were saved earlier.
- Songs screen can clear recents; selecting from recents wires the side panel session for queue context.

### Media notification & session (Media3)

- When the player screen is active, a **foreground-style media notification** shows title, artist, **colorized** notification (accent from **Palette** over downloaded artwork), large icon, and transport controls (previous / play / pause / next).
- **In-memory cache** in `PlayerNotificationController` for **artwork bitmap** and **accent color** (skips re-fetch when the URL is unchanged).
- **ExoPlayer** backs a `ForwardingPlayer`; **`UiSyncedForwardingPlayer`** maps **Compose / `PlayerUiState`** into the Media3 `Player` surface for `PlayerNotificationManager` and `MediaSession`. A **listener filter** suppresses spurious “not playing / IDLE” events from the idle **ExoPlayer** (no real media) so notification **play/pause** stays in sync with the mock UI.
- **Play / pause** from the notification apply explicit play/pause state to the ViewModel (not a blind toggle), matching Media3 callbacks.
- Tapping the notification returns to the app via **`MainActivity`** `singleTop`; the deep-link **route** is stored in **SharedPreferences** and consumed in `SimplePlayerNavHost`.
- **POST_NOTIFICATIONS** permission is requested on API 33+ from the player route.

### Adaptive UI

- **Breakpoint** at `600dp` width (`SimplePlayerBreakpoints`) for tablet vs phone layouts.
- **Handheld orientation policy**: phones (physical smallest width &lt; 600dp) lock **portrait**; larger devices allow unspecified orientation—see `HandheldOrientation.kt` (uses physical display metrics to avoid split-screen misclassification).

### Quality

- **ktlint** Gradle plugin on main and test sources.
- **Unit tests** (JUnit, coroutines test, Robolectric where needed) for mappers, navigation routes, player helpers, notification mapping, etc.

---

## CI & GitHub Actions

- **CI** (on every push / PR to `main`): runs **unit tests** and **ktlint** so `main` stays green.
- **Build APK** (manual workflow or `v*` tags): produces a **debug** APK artifact for testing or attaching to a GitHub Release.

See **[docs/GITHUB_ACTIONS.md](docs/GITHUB_ACTIONS.md)** for triggers, how to download artifacts, optional signed release setup, and branch protection tips.

---

## Architecture

High-level layers:

| Layer | Responsibility |
|--------|----------------|
| **`presentation/`** | Composable screens & routes, `ViewModel`s, UI state, navigation glue. |
| **`domain/`** | `Song`, album models, `SongRepository` interface, use cases (`SearchSongsUseCase`, `GetAlbumDetailUseCase`). |
| **`data/`** | `SongRepositoryImpl`, Retrofit `SongsRemoteDataSource`, DTOs & mappers, `SongsSearchRepository` (shared search state + effects), `PlayerSidePanelSession`, `RecentSongsRepository`. |
| **`core/`** | Time formatting, queue skip index math, artwork URL helpers, orientation policy, coroutine dispatchers. |
| **`media/`** | `PlayerNotificationController`, `UiSyncedForwardingPlayer`, `PlayerListenerForUiFilter`, `PlayerNotificationTransport`, palette accent, notification constants & Hilt entry point. |
| **`di/`** | Hilt modules (`DataModule`, `NetworkModule`, `GsonModule`, `CoroutinesModule`). |
| **`ui/theme/`** | Colors, typography (Articulat CF—see font licensing note in `Type.kt`), dimens, breakpoints, theme. |

**Navigation**

- `SplashActivity` → `MainActivity` (Compose host).
- `SimplePlayerNavHost`: `songs` → `album/{collectionId}` → `player/...` with typed `navArgument`s and slide transitions.

**State**

- Player args restored via **`SavedStateHandle`** and `PlayerNavigation` constants.
- Search / side panel coordinated through **`SongsSearchRepository`** + **`PlayerSidePanelSession`**.

---

## Tech stack & libraries

Versions are centralized in [`gradle/libs.versions.toml`](gradle/libs.versions.toml). Highlights:

| Area | Library | Notes |
|------|---------|--------|
| Language / tooling | Kotlin **2.0.21**, AGP **8.9.2**, KSP **2.0.21-1.0.28** | |
| UI | Jetpack Compose (BOM **2024.09.00**), Material 3, Material icons extended, Navigation Compose **2.8.4**, Activity Compose **1.13.0** | |
| Lifecycle | Lifecycle **2.10.0** (runtime, ViewModel Compose) | |
| DI | Hilt **2.52** + `hilt-navigation-compose` **1.2.0** | |
| Networking | Retrofit **2.11.0**, Gson **2.11.0** | |
| Images | Coil **2.7.0** | |
| Animation | Lottie Compose **6.6.2** | |
| Persistence | DataStore Preferences **1.1.1** | |
| Media | Media3 **1.5.1** (ExoPlayer, session, UI), AndroidX Media **1.7.0**, Palette **1.0.0** | |
| Coroutines | kotlinx-coroutines **1.9.0** (incl. `kotlinx-coroutines-test`) | |
| Splash | Core Splashscreen **1.2.0** | |
| Testing | JUnit **4.13.2**, Robolectric **4.14.1**, AndroidX Test / Espresso / Compose UI Test (Android) | |
| Lint / style | ktlint Gradle **12.2.0** | |

Also uses **AndroidX Core KTX** and **Core Splashscreen** as configured in `app/build.gradle.kts`.

---

## Key decisions

1. **Mock playback instead of ExoPlayer audio pipeline** — Keeps the sample focused on UI, navigation, and MediaSession/notification integration without audio focus, DRM, or codec concerns.
2. **No `PlayerNotificationController` inside `PlayerViewModel`** — Notification updates are driven from the Composable layer (`PlayerRoute`) via `LaunchedEffect` + `DisposableEffect` to avoid a past Hilt graph edge case; `PlayerNotificationController` remains a singleton `@Inject` from the application.
3. **`UiSyncedForwardingPlayer` + mapping** — Bridges UI state to Media3 `Player` APIs; **`PlayerListenerForUiFilter`** drops delegate idle/`isPlaying=false` noise from the idle **ExoPlayer** so the notification does not immediately undo mock play.
4. **Explicit Retrofit query parameters** — All iTunes `search` / `lookup` query params are required in the API interface so Kotlin/Retrofit never omit them and change API behaviour.
5. **Physical smallest width for orientation** — Avoids treating a tablet in split-screen as a phone for layout/orientation.
6. **Fonts** — `Type.kt` documents that demo Articulat CF fonts require a proper license for Play Store release.

---

## Reference documentation

- [Android developers — Jetpack Compose](https://developer.android.com/jetpack/compose)
- [Navigation Compose](https://developer.android.com/jetpack/androidx/releases/navigation)
- [Hilt](https://developer.android.com/training/dependency-injection/hilt-android)
- [Media3 — MediaSession, PlayerNotificationManager](https://developer.android.com/guide/topics/media/media3)
- [Retrofit](https://square.github.io/retrofit/)
- [iTunes Search API](https://performance-partners.apple.com/search-api) (Apple)
- [DataStore](https://developer.android.com/topic/libraries/architecture/datastore)
- [Coil](https://coil-kt.github.io/coil/compose/)
- [ktlint](https://pinterest.github.io/ktlint/)

---

## Project commands

Run from the repository root (use the Gradle Wrapper):

```bash
# Debug APK
./gradlew :app:assembleDebug

# Unit tests (debug)
./gradlew :app:testDebugUnitTest

# Kotlin lint (fails on violations)
./gradlew :app:ktlintCheck

# Compile only (debug Kotlin)
./gradlew :app:compileDebugKotlin

# Clean build (useful after dependency or Hilt graph changes)
./gradlew clean :app:assembleDebug
```

**Android Studio**: open the project folder, sync Gradle, then run the `app` configuration on an emulator or device.

---

## Compatibility & possibilities

### Current compatibility

| | Target |
|----|--------|
| **minSdk** | 28 (Android 9) |
| **targetSdk** | 35 |
| **compileSdk** | 36 |
| **Java / Kotlin JVM** | 11 |

### Permissions

- `INTERNET` — iTunes API calls.
- `POST_NOTIFICATIONS` — media notification on Android 13+ (runtime request from player).

### Possible extensions

- Real playback with **ExoPlayer** + `MediaItem` from remote URLs (where permitted by ToS).
- **Background** playback service and persistent `MediaSession` when the app leaves the foreground.
- **Offline-first search** with a local database (Room) or full catalog cache in addition to **DataStore** recents.
- **Instrumentation / screenshot** tests for critical flows.

---

## License & attribution

This project is a sample application. **iTunes Search API** use is subject to [Apple’s terms](https://www.apple.com/legal/internet-services/itunes/dev/stdeula/). Verify font licensing in `ui/theme/Type.kt` before any store release.
