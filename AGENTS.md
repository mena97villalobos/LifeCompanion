# AGENTS.md — LifeCompanion

Guidance for AI coding assistants and automation navigating this repository.

## Project snapshot

- **What**: **LifeCompanion** — shared **Compose Multiplatform** UI (`:composeApp`), warranty
  tracking (local persistence + remote services), dashboard with exchange rates. The **Android**
  `:app` module is a thin shell (Firebase, Koin bootstrap). An **iOS** host lives under `iosApp/` (
  Xcode project via XcodeGen).
- **Language**: Kotlin **2.3.x**, **Java 17** (source/target and toolchain).
- **Build**: Gradle with **Kotlin DSL** (`*.gradle.kts`), **version catalog** at `gradle/libs.versions.toml`, `includeBuild("build-logic")` for shared convention code.
- **Min/target SDK**: **API 36** (with minor API level 1 where used in `compileSdk` blocks). Do not assume lower API levels without checking.

## Module map (dependency direction)

```
app
├── composeApp      (shared screens, Navigation 3, ViewModels, `composeAppKoinModule`)
├── design-system   (Compose UI primitives, theme, reusable components)
├── domain          (models, repository interfaces, use cases)
├── data:local      (Room, entities, DAOs, local repository impl, Koin `localModule` / `iosLocalModule`, warranty use cases)
└── data:remote     (Ktor/HTTP, MinIO, exchange API; KMP Android+iOS — Koin `remoteModule` / `iosRemoteModule`)
```

- **`domain`**: Kotlin Multiplatform library (Android + iOS targets); **no** feature UI in domain by
  convention. Provides `createDefaultDispatcherService()` for `DispatcherService`; hosts wire it in
  Koin. Depends on coroutines and datetime in `commonMain`.
- **`data:local`**: Depends on **`domain`**. Uses the KMP **library** convention (no Compose). Room
  **schemas** live under `data/local/schemas/` (Room `schemaDirectory`).
- **`data:remote`**: Kotlin Multiplatform (Android + iOS). Depends on **`domain`**. Uses the KMP *
  *library** convention (no Compose). **BuildKonfig** fields for exchange API and MinIO come from
  Gradle project properties (see [Secrets & local config](#secrets--local-config)). Android uses the
  JVM MinIO SDK; iOS uploads via Ktor + SigV4 (`MinioS3KtorService`). An iOS app must start Koin
  with the same graph as Android (see `MainApplication`): **`DispatcherService`** (
  `createDefaultDispatcherService()`), **`iosLocalModule`** (includes DB, `WarrantyRepository`, and
  **`warrantyUseCaseModule`**), and **`iosRemoteModule`**.
- **`design-system`**: Kotlin Multiplatform + **Compose Multiplatform** (same
  `lifecompanion-kotlin-multiplatform-compose-plugin` convention as documented in this file).
  Depends on **`domain`**; shared UI in **`commonMain`**, Android-specific pieces (e.g.
  `ImagePicker`, `@Preview`) in **`androidMain`**, iOS theme actual in **`iosMain`**. Spacing/sizing
  uses **`DesignSystemDimens`** tokens instead of Android `R.dimen`.
- **`composeApp`**: Kotlin Multiplatform + **Compose Multiplatform** (
  `lifecompanion-kotlin-multiplatform-compose-plugin`). **`commonMain`**: feature UI, **Navigation 3
  ** (`NavDisplay` / `NavEntry`), shared **ViewModels** (multiplatform
  `androidx.lifecycle.ViewModel`), **`composeAppKoinModule`** (`coroutinesModules` +
  `viewModelModule`). **`iosMain`**: `initializeLifeCompanionKoinForIos()` and
  `createLifeCompanionRootViewController()` for the iOS host.
- **`app`**: Android application shell only: **`MainApplication`** (Firebase, Koin
  `androidContext()`), **`MainActivity`** (`setContent { LifeCompanionApp() }`). Depends on *
  *`:composeApp`**, **`:data:local`**, **`:data:remote`** for `appModule` (`composeAppKoinModule`,
  `localModule`, `remoteModule`).

When adding a feature, prefer: **domain** (contracts + use cases) → **data** (implementations) → *
*composeApp** (screens/ViewModels) and reuse **`design-system`** for shared UI primitives.

## Package roots (where to look)

| Module          | Base package                         | Typical contents                                                                                                                                    |
|-----------------|--------------------------------------|-----------------------------------------------------------------------------------------------------------------------------------------------------|
| `app`           | `com.mena97villalobos.lifecompanion` | `MainActivity`, `MainApplication`, `di/AppModule.kt` (Koin aggregation only)                                                                        |
| `composeApp`    | `com.mena97villalobos.lifecompanion` | `LifeCompanionApp.kt`, `navigation/`, `ui/`, `di/` (`composeAppKoinModule`, ViewModels)                                                             |
| `design-system` | `com.mena97villalobos.designsystem`  | `commonMain` (`theme/`, `cards/`, `button/`, `tokens/`); `androidMain` (previews); `iosMain` (theme actual, `ImagePicker`)                          |
| `domain`        | `com.mena97villalobos.domain`        | `model/`, `repository/`, `usecases/`, `services/` (interfaces)                                                                                      |
| `data:local`    | `com.mena97villalobos.local`         | `database/`, `dao/`, `entities/`, `repository/`, `mappers/`, `di/` (`LocalModule.kt`, `WarrantyUseCaseModule.kt`, `LocalModule.ios.kt`)             |
| `data:remote`   | `com.mena97villalobos.remote`        | `commonMain` (Ktor, exchange API only: `remoteCoreModule`); `androidMain` (`RemoteModule`, MinIO JVM); `iosMain` (`iosRemoteModule`, Darwin engine) |

## Architecture conventions

- **DI**: [Koin](https://insert-koin.io/). `app/.../di/AppModule.kt` includes
  `composeAppKoinModule`, `localModule`, and `remoteModule`. ViewModels are registered in
  `composeApp/.../di/ViewModelModule.kt`.
- **UI
  **: [Compose Multiplatform](https://kotlinlang.org/docs/multiplatform/compose-multiplatform.html)
  in **`composeApp`** with **Navigation 3
  ** ([Kotlin doc](https://kotlinlang.org/docs/multiplatform/compose-navigation-3.html)) —
  `NavDisplay`, `NavEntry`, user-owned back stack. Shared **ViewModels** use the multiplatform
  Lifecycle stack ([doc](https://kotlinlang.org/docs/multiplatform/compose-viewmodel.html)).
- **Async**: Kotlin **Coroutines**; dispatchers abstracted via domain `DispatcherService` where used.
- **Local DB**: **Room** (KSP-generated code under `data/local/build/generated/` — **do not edit**; edit entities/DAO/database classes in `src/main` only).
- **Networking / cloud**: **Ktor** (Koin), **MinIO** (JVM SDK on Android; S3 SigV4 + Ktor on iOS), **Firebase** in the app module where enabled. Exchange rates: remote API + repository pattern in domain.

## Build & quality

- **Dependencies**: Add versions in `gradle/libs.versions.toml`, reference via `libs.*` in module `build.gradle.kts` files.
- **Repositories**: `settings.gradle.kts` uses `FAIL_ON_PROJECT_REPOS`; declare repos only in the root settings file.
- **Static analysis**: Detekt is set up via `build-logic` (`DetektConventionPlugin.kt`), config under **`.detekt/detekt-rules.yml`**, optional per-module **`detekt-baseline.xml`**. Compose-specific and ktlint-wrapper rules are on the classpath.
- **Kotlin Multiplatform + Compose Multiplatform (new modules)**: A build-logic convention plugin (`KotlinMultiplatformComposeConventionPlugin.kt`, id `com.mena97villalobos.lifecompanion.kotlin.multiplatform.compose.plugin`) applies **`org.jetbrains.kotlin.multiplatform`**, **`com.android.kotlin.multiplatform.library`** (required for KMP + Android on AGP 9+), **`org.jetbrains.compose`**, and **`org.jetbrains.kotlin.plugin.compose`** (same version as Kotlin in the catalog). Use it on a new module with `alias(libs.plugins.lifecompanion.kotlin.multiplatform.compose.plugin)` in that module's `plugins { }` block, then add a **`kotlin { android { ... } }`** block, source sets (`commonMain`, `androidMain`, etc.), and dependencies as in the [Android KMP library plugin](https://developer.android.com/kotlin/multiplatform/plugin) and [Compose compiler (KMP)](https://kotlinlang.org/docs/multiplatform/compose-compiler.html) docs. Versions: **`composeMultiplatform`** and related plugin aliases live in `gradle/libs.versions.toml`; the root `build.gradle.kts` declares those plugins with `apply false` so Gradle can resolve them when the convention runs.
- **Kotlin Multiplatform library without Compose**: **`domain`** and similar modules use
  `KotlinMultiplatformLibraryConventionPlugin.kt` (id
  `com.mena97villalobos.lifecompanion.kotlin.multiplatform.library.plugin`) — only *
  *`org.jetbrains.kotlin.multiplatform`** and **`com.android.kotlin.multiplatform.library`**, so no
  Compose runtime or compiler dependency is required.
- **Gradle performance**: Configuration cache and parallel builds enabled in `gradle.properties` — avoid patterns that break configuration cache unless you fix them.

## Secrets & local config

- **`data/remote`** expects Gradle `project` properties such as `exchangeBaseUrl`, `exchangeApiKey`, `minioEndpoint`, `minioEndpointAccessKey`, `minioEndpointSecretKey`, `minioBucketName` (see `data/remote/build.gradle.kts`). These may be supplied via **`gradle.properties`** (often gitignored for secrets) or **`local.properties`** — **never commit real API keys**.
- **`google-services.json`** (Firebase) is required for Firebase-enabled builds; treat as environment-specific.

## What to avoid

- Do not hand-edit **KSP/Room generated** sources under `**/build/generated/**`.
- Do not add **duplicate repository** declarations inside individual modules (root `settings.gradle.kts` owns this).
- Prefer **small, focused changes** matching existing naming (`*Repository`, `*UseCase`, `*ViewModel`, `*Screen`).

## Quick file index

| Topic                         | Location                                                                                                                                |
|-------------------------------|-----------------------------------------------------------------------------------------------------------------------------------------|
| App + Koin bootstrap          | `app/.../MainApplication.kt`, `app/.../di/AppModule.kt`                                                                                 |
| Shared UI + navigation        | `composeApp/.../LifeCompanionApp.kt`, `composeApp/.../navigation/AppRoute.kt`                                                           |
| iOS host (XcodeGen)           | `iosApp/project.yml`, `iosApp/LifeCompanion/*.swift`                                                                                    |
| Version catalog               | `gradle/libs.versions.toml`                                                                                                             |
| Module list                   | `settings.gradle.kts`                                                                                                                   |
| Detekt                        | `.detekt/detekt-rules.yml`, `build-logic/convention/.../DetektConventionPlugin.kt`                                                      |
| KMP + Compose MP (convention) | `build-logic/convention/.../KotlinMultiplatformComposeConventionPlugin.kt`, catalog `lifecompanion-kotlin-multiplatform-compose-plugin` |
| KMP library without Compose   | `build-logic/convention/.../KotlinMultiplatformLibraryConventionPlugin.kt`, catalog `lifecompanion-kotlin-multiplatform-library-plugin` |

## iOS app (`iosApp/`)

1. Build the **ComposeApp** framework for the simulator (or device):
   `./gradlew :composeApp:linkDebugFrameworkIosSimulatorArm64` (or
   `:composeApp:linkDebugFrameworkIosArm64`).
2. Install [XcodeGen](https://github.com/yonaskolb/XcodeGen): `brew install xcodegen`.
3. From `iosApp/`, run `xcodegen generate` to produce `LifeCompanion.xcodeproj`, then open it in
   Xcode.
4. The Swift app calls `IosComposeEntryKt.initializeLifeCompanionKoinForIos()` and embeds
   `createLifeCompanionRootViewController()` (see `iosApp/LifeCompanion/`). Koin must mirror
   Android: `composeAppKoinModule`, `iosLocalModule`, `iosRemoteModule` (wired in
   `IosComposeEntry.kt`).
5. Treat `iosApp/project.yml` as the source of truth for iOS project structure; regenerate
   `LifeCompanion.xcodeproj` via `xcodegen generate` instead of editing generated Xcode files
   directly.

## Suggested verification commands

Run from the repo root (adjust if using a Gradle wrapper with a different invocation):

```bash
./gradlew :app:assembleDebug
./gradlew :composeApp:compileKotlinIosArm64
./gradlew detekt
```

If the wrapper is absent, use the system `gradle` with the same tasks.

---

*Update this file when modules, package names, or architectural rules change.*
