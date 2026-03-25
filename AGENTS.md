# AGENTS.md — LifeCompanion

Guidance for AI coding assistants and automation navigating this repository.

## Project snapshot

- **What**: Android app **LifeCompanion** — Jetpack Compose UI, warranty tracking (local persistence + remote services), dashboard with exchange rates.
- **Language**: Kotlin **2.3.x**, **Java 17** (source/target and toolchain).
- **Build**: Gradle with **Kotlin DSL** (`*.gradle.kts`), **version catalog** at `gradle/libs.versions.toml`, `includeBuild("build-logic")` for shared convention code.
- **Min/target SDK**: **API 36** (with minor API level 1 where used in `compileSdk` blocks). Do not assume lower API levels without checking.

## Module map (dependency direction)

```
app
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
- **`app`**: Wires everything: **`MainApplication`** starts **Koin** with `appModule` (includes coroutines, `localModule`, `remoteModule`, `viewModelModule`). Feature UI lives under `app/.../ui/`.

When adding a feature, prefer: **domain** (contracts + use cases) → **data** (implementations) → **app** (screens/ViewModels) and reuse **`design-system`** for shared UI.

## Package roots (where to look)

| Module          | Base package                         | Typical contents                                                                                                                                    |
|-----------------|--------------------------------------|-----------------------------------------------------------------------------------------------------------------------------------------------------|
| `app`           | `com.mena97villalobos.lifecompanion` | `MainActivity`, `MainApplication`, `di/`, `ui/` (screens, `navigation/`, feature folders)                                                           |
| `design-system` | `com.mena97villalobos.designsystem`  | `commonMain` (`theme/`, `cards/`, `button/`, `tokens/`); `androidMain` (previews, `ImagePicker`); `iosMain` (theme actual)                          |
| `domain`        | `com.mena97villalobos.domain`        | `model/`, `repository/`, `usecases/`, `services/` (interfaces)                                                                                      |
| `data:local`    | `com.mena97villalobos.local`         | `database/`, `dao/`, `entities/`, `repository/`, `mappers/`, `di/` (`LocalModule.kt`, `WarrantyUseCaseModule.kt`, `LocalModule.ios.kt`)             |
| `data:remote`   | `com.mena97villalobos.remote`        | `commonMain` (Ktor, exchange API only: `remoteCoreModule`); `androidMain` (`RemoteModule`, MinIO JVM); `iosMain` (`iosRemoteModule`, Darwin engine) |

## Architecture conventions

- **DI**: [Koin](https://insert-koin.io/). App entry: `app/.../di/AppModule.kt` aggregates modules. Feature ViewModels are registered in `ViewModelModule.kt`; data layers expose `localModule` / `remoteModule`.
- **UI**: [Jetpack Compose](https://developer.android.com/jetpack/compose) + **Navigation Compose**. Central graph: `app/.../ui/AppNavHost.kt`. Routes and bottom-nav metadata: `AppScreens` in `ui/navigation/AppScreens.kt`.
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
| Navigation                    | `app/.../ui/AppNavHost.kt`, `app/.../ui/navigation/AppScreens.kt`                                                                       |
| Version catalog               | `gradle/libs.versions.toml`                                                                                                             |
| Module list                   | `settings.gradle.kts`                                                                                                                   |
| Detekt                        | `.detekt/detekt-rules.yml`, `build-logic/convention/.../DetektConventionPlugin.kt`                                                      |
| KMP + Compose MP (convention) | `build-logic/convention/.../KotlinMultiplatformComposeConventionPlugin.kt`, catalog `lifecompanion-kotlin-multiplatform-compose-plugin` |
| KMP library without Compose   | `build-logic/convention/.../KotlinMultiplatformLibraryConventionPlugin.kt`, catalog `lifecompanion-kotlin-multiplatform-library-plugin` |

## Suggested verification commands

Run from the repo root (adjust if using a Gradle wrapper with a different invocation):

```bash
./gradlew :app:assembleDebug
./gradlew detekt
```

If the wrapper is absent, use the system `gradle` with the same tasks.

---

*Update this file when modules, package names, or architectural rules change.*
