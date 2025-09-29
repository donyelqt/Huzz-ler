# Huzzler

Huzzler is a modern e-learning and productivity companion for students. It blends rich visual dashboards, gamified rewards, and AI-assisted study tools to keep learners engaged and on track.

## Feature Highlights
- **Dashboard insights**: Red-themed hero section with personalized greeting, progress stats, and assignment feed powered by `DashboardFragment.kt` and structured stat cards in `layout_dashboard_stat_card.xml`.
- **Assignment tracking**: Material design cards (`item_assignment.xml`) show priority, difficulty, due date, and reward points with chips and gradient backgrounds for clarity.
- **Rewards marketplace**: Fully designed Compose experience in `ui/rewards/` showcasing categories, placeholder imagery, and redeem actions. Asset guidance lives in `app/src/main/assets/images/rewards/README.md`.
- **Navigation shell**: Bottom navigation managed by `MainActivity.kt` with Jetpack Navigation to switch between Dashboard, Rewards, Chat, and Profile destinations.
- **Robust foundation**: Hilt-powered dependency injection, Retrofit networking stubs, Room dependencies, and Kotlin coroutines provide room for future data features.

## Tech Stack
- **Language**: Kotlin
- **UI**: XML View system with ViewBinding, Jetpack Compose (Material 3), Material Components, ConstraintLayout
- **Architecture**: MVVM, AndroidX ViewModel & LiveData, Navigation Component
- **DI**: Hilt (Dagger)
- **Networking**: Retrofit + OkHttp
- **Persistence**: Room (runtime + KTX)
- **Async**: Kotlin Coroutines
- **Image Loading**: Glide
- **Testing**: JUnit, Espresso, Compose UI Test

## Project Structure
```
app/
├─ build.gradle.kts        # Android application module configuration
├─ src/main/
│  ├─ java/com/example/huzzler/
│  │  ├─ HuzzlerApplication.kt
│  │  ├─ MainActivity.kt
│  │  ├─ data/             # Data sources, repositories (scaffolding)
│  │  └─ ui/               # Feature packages (dashboard, rewards, etc.)
│  ├─ res/                 # Layouts, drawables, shared resources
│  └─ assets/images/rewards/README.md  # Reward imagery guidelines
└─ ...
```

## Getting Started
1. **Prerequisites**
   - Android Studio Ladybug+ (AGP 8.6) with Kotlin 2.0 toolchain support
   - JDK 17 (Gradle wrapper enforces Java 17)
   - Android SDK 24+ (target SDK 36)
2. **Clone & open**
   ```bash
   git clone <your-fork-or-repo-url>
   cd Huzzler
   ```
   Open the project in Android Studio and let Gradle sync.
3. **Build variants**
   - `debug` (default) and `release` configurations defined in `app/build.gradle.kts`
4. **Run the app**
   - Choose a device or emulator running API 24+
   - Click **Run ▶** in Android Studio or execute `./gradlew installDebug`

## Reward Assets
- Placeholder images live under `app/src/main/assets/images/rewards/`
- Replace them with 400x400+ PNG/JPG files following the instructions in the directory README.

## Testing & Quality
- **Unit tests**: `./gradlew test`
- **Instrumentation & Compose UI tests**: `./gradlew connectedAndroidTest`
- Jetpack Compose compiler version is pinned to `1.5.15` (see `gradle/libs.versions.toml` and `app/build.gradle.kts`).

## Troubleshooting
- **Compose compiler errors**: Ensure the Kotlin and Compose versions match the entries in `gradle/libs.versions.toml`.
- **Resource linking failures**: Confirm drawable and color names exist after any theming changes (e.g., stats card gradients in `drawable/stat_card_glass_bg.xml`).

## Contributions & License
- Forks and contribution ideas are welcome. Open issues or PRs describing planned enhancements.
- License: _TBD_. Add your preferred license text under `LICENSE` if required.
