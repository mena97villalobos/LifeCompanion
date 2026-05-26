# `:data:local` — Room KMP local database

Offline-first persistence for LifeCompanion, built on **Jetpack Room KMP** (`androidx.room`) with the
**bundled SQLite** driver. Room annotations compile via **KSP** to per-target implementations
(`kspAndroid`, `kspIosArm64`, `kspIosSimulatorArm64`, `kspIosX64`).

This module targets **Android + iOS only**. There is no Desktop/JVM target in this project, so the
issue's `desktopMain` builder / `~/.lifecompanion/` location does not apply.

## Layout

| Path | Contents |
|------|----------|
| `database/LifeCompanionDatabase.kt` | `@Database` class, `@ConstructedBy` + `expect object LifeCompanionDatabaseConstructor`, and `getRoomDatabase(builder)` (wires `BundledSQLiteDriver` + `setQueryCoroutineContext`). |
| `database/LifeCompanionDatabaseBuilder.android.kt` / `.ios.kt` | Per-platform `RoomDatabase.Builder` factories (file paths below). |
| `database/RoomQueryCoroutineContext.kt` (+ `.android` / `.ios`) | `expect/actual` query dispatcher: `Dispatchers.IO` on Android, `Dispatchers.Default` on iOS. |
| `entities/WarrantyEntity.kt`, `dao/WarrantyDao.kt` | The starter schema (the issue's placeholder `User` was replaced by the real `warranties` table). |
| `di/LocalCoreModule.kt`, `di/LocalModule.kt`, `di/LocalModule.ios.kt` | Koin wiring — registers `LifeCompanionDatabase` as a **singleton**. |
| `schemas/` | Exported Room schema JSON, committed to source control. |

## Where the database file lives per platform

| Platform | Location | Source |
|----------|----------|--------|
| **Android** | App-private storage: `/data/data/com.mena97villalobos.lifecompanion/databases/app_database` | `context.getDatabasePath("app_database")` in `LifeCompanionDatabaseBuilder.android.kt` |
| **iOS** | App sandbox `Documents/` directory: `<app sandbox>/Documents/app_database` | `NSDocumentDirectory` + `/app_database` in `LifeCompanionDatabaseBuilder.ios.kt` |
| **Desktop** | N/A — no Desktop/JVM target in this project | — |

Both locations are persistent (not caches/tmp), so the database survives app restarts.

## Dependency injection

The database is provided as a Koin singleton:

- **Android** — `localModule` calls `getRoomDatabase(getLifeCompanionDatabaseBuilder(androidContext()))`.
  Requires `androidContext()` to be set when starting Koin (see `app/.../MainApplication.kt`).
- **iOS** — `iosLocalModule` calls `getRoomDatabase(getLifeCompanionDatabaseBuilder())` (no context).

`localCoreModule` (shared) then exposes `single<WarrantyDao> { get<LifeCompanionDatabase>().warrantyDao() }`,
the `WarrantyRepository`, and `warrantyUseCaseModule`.

## Schema export

Schemas are exported to `data/local/schemas/` (configured via `room { schemaDirectory(...) }` and the
`room.schemaLocation` KSP arg) and committed. Bump `version` in `@Database` and add an
`@AutoMigration`/`Migration` when changing the schema.

## Tests

DAO behaviour is verified against a real Room + bundled-SQLite stack using
`Room.inMemoryDatabaseBuilder`:

- **iOS (plain unit test, no device needed):**
  `src/iosTest/.../dao/WarrantyDaoTest.kt` — run with
  `./gradlew :data:local:iosSimulatorArm64Test`.
- **Android (instrumented — `inMemoryDatabaseBuilder` needs a `Context`):**
  `src/androidDeviceTest/.../dao/WarrantyDaoInstrumentedTest.kt` — run with
  `./gradlew :data:local:connectedAndroidTest` (requires a device/emulator).

Both cover insert → query → update → delete.
