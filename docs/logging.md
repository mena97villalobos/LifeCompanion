# Logging & Crash Reporting Conventions

LifeCompanion uses a single multiplatform observability layer, the **`:core:observability`**
module, built on:

- **[Kermit](https://kermit.touchlab.co/)** — multiplatform logging that bridges to each platform's
  native logger.
- **[Sentry KMP](https://docs.sentry.io/platforms/kotlin-multiplatform/)** — shared crash & error
  reporting for Android and iOS.

All logging and crash reporting go through this module. Do **not** add `println`, `Log.d`,
`NSLog`, or other ad-hoc logging elsewhere.

> Scope: V1 ships Android and iOS. There is no Desktop/JVM target yet; when one is added, Kermit's
> default sink covers stdout automatically and no observability code changes are required.

---

## Initialization

`Observability.init(isDebug)` is called once at startup, **before** Koin and any logging:

- **Android** — `MainApplication.onCreate()` with `isDebug = BuildConfig.DEBUG`.
- **iOS** — `IosComposeEntry.initializeLifeCompanionKoinForIos()` with `isDebug = Platform.isDebugBinary`.

It configures the Kermit severity floor, installs the PII-scrubbing + platform + crash sinks, and
initializes Sentry (when a DSN is configured). The `CrashReporter` is also available via Koin
(`observabilityModule`).

---

## Severity levels — when to use each

| Level | Use for | Visible in |
|-------|---------|-----------|
| `Verbose` | Fine-grained tracing while developing a feature | Debug builds only |
| `Debug` | Diagnostic detail useful when reproducing a bug | Debug builds only |
| `Info` | Notable lifecycle/business events (loan created, schedule recomputed, payment logged) | Debug + release |
| `Warn` | Recoverable problems / unexpected-but-handled states (reconciliation gap, stale FX rate) | Debug + release |
| `Error` | Failures that need attention; **also forwarded to Sentry** | Debug + release |
| `Assert` | "Should never happen" invariants; **also forwarded to Sentry** | Debug + release |

The severity floor is set automatically: **Verbose** and above in debug builds, **Info** and above
in release builds.

Logs at `Error`/`Assert` are routed to Sentry: a log with a throwable becomes a captured exception;
without one it becomes a captured message.

### Usage

```kotlin
import co.touchlab.kermit.Logger

private val log = Logger.withTag("LoanEngine")

log.i { "schedule recomputed for loan $loanId ($periods periods)" }
log.e(throwable) { "failed to persist payment for loan $loanId" }
```

---

## What to NEVER log

Per the BRD (§11.1), the following must never appear in logs, in any build:

- **Monetary amounts** — payment amounts, loan principals, balances, fees (e.g. `₡1.234.567,89`, `$1,234.56`).
- **Personal data (PII)** — names, email addresses, phone numbers, addresses.
- **Secrets** — PINs, passphrases, recovery phrases, API keys, tokens, DSNs.

Treat this as a hard rule: log **identifiers and counts**, never the sensitive values themselves.
Log `loan $loanId`, not the loan's balance; log `user action started`, not the user's name.

### Automatic redaction (safety net)

In **release builds**, every log message is passed through `redact()` (see
`PiiScrubbingLogWriter`) before reaching any sink — including Sentry — which masks:

- email addresses → `[REDACTED_EMAIL]`
- monetary amounts (₡/$ and CRC/USD) → `[REDACTED_AMOUNT]`

In **debug builds** redaction is disabled so developers see full messages locally.

Redaction is a backstop, **not** a substitute for the discipline above: names and other free-text
PII cannot be detected automatically, and exception messages/stack traces are sent to Sentry
unredacted — so never embed sensitive values in messages or exceptions.

---

## Correlation IDs

Every user-initiated action should carry a correlation ID so all of its log statements can be tied
together (and matched against a Sentry event).

```kotlin
import com.mena97villalobos.observability.CorrelationContext
import com.mena97villalobos.observability.newCorrelationId
import com.mena97villalobos.observability.withCorrelation

// At an action entry point (e.g. a ViewModel handler / use case):
val cid = newCorrelationId()
withContext(CorrelationContext(cid)) {
    val log = Logger.withTag("LogPayment").withCorrelation(cid)
    log.i { "started" }      // → "LogPayment cid=<id> started"
    // ...downstream work; read the active id anywhere via currentCorrelationId()
}
```

Convention: obtain the ID once at the start of the action with `newCorrelationId()`, put it in a
`CorrelationContext`, and use `Logger.withCorrelation(cid)` so it appears on every line. The active
ID is retrievable in suspend code via `currentCorrelationId()`.

IDs use the Kotlin stdlib `kotlin.uuid.Uuid` — no third-party dependency.

---

## Crash reporting (Sentry)

- Crashes and `Error`/`Assert` logs are reported to Sentry on both platforms via the shared
  `CrashReporter`.
- Every event is tagged with `build_type` (debug/release), `platform` (android/ios) and
  `app_version`.
- The **DSN is never hardcoded**. It is injected at build time via `BuildKonfig` from the
  `sentry.dsn` Gradle property (set in `local.properties`, `~/.gradle/gradle.properties`, or a CI
  secret). Related properties: `sentry.environment` (default `development`) and `app.versionName`.
- When no DSN is configured (the default for local/dev builds), crash reporting is a no-op — logging
  still works normally.

```properties
# local.properties / gradle.properties (do not commit real DSNs)
sentry.dsn=https://examplePublicKey@o0.ingest.sentry.io/0
sentry.environment=production
```

### iOS — linking the Sentry Cocoa framework

`sentry-kotlin-multiplatform` binds to the Sentry **Cocoa** SDK, which must be linked into the iOS
app. This project uses XcodeGen (no committed `.xcodeproj`, no CocoaPods), so Sentry Cocoa is
declared as an **SPM package** in `iosApp/project.yml` (pinned to the version the KMP SDK expects).
After regenerating and building, Xcode resolves the package and the crash-reporting symbols link at
app build time:

```bash
cd iosApp && xcodegen generate && open LifeCompanion.xcodeproj
# Xcode resolves the Sentry SPM package on first build.
```

> Running the module's iOS **unit tests** through Gradle (`:core:observability:iosSimulatorArm64Test`)
> links a native test executable and therefore also needs the Sentry Cocoa framework on the linker
> path; without it you'll see `ld: framework 'Sentry' not found`. The pure logic (redaction,
> correlation IDs) is covered by `:core:observability:testAndroidHostTest`, which is what CI and
> local fast checks run.

---

## Platform sinks

Kermit's default platform writer routes to each platform's native logger automatically:

- **Android** — Logcat
- **iOS** — `os_log` / NSLog (visible in the Xcode console)

No per-platform sink configuration is needed.
