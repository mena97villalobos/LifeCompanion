package com.mena97villalobos.observability

import co.touchlab.kermit.Logger
import co.touchlab.kermit.Severity
import co.touchlab.kermit.platformLogWriter
import io.sentry.kotlin.multiplatform.Sentry

/**
 * Entry point for the observability layer. Call [init] exactly once at app startup (Android
 * `Application.onCreate`, iOS app init) before any logging or DI resolution.
 *
 * It wires three things together:
 *  - **Kermit** logging with a severity floor that depends on the build type (Verbose in debug,
 *    Info in release) and platform-native sinks (Logcat on Android, os_log/NSLog on iOS).
 *  - **PII scrubbing** of every release-build log line before it reaches any sink.
 *  - **Sentry** crash reporting (when a DSN is configured), with events tagged by build type,
 *    platform and app version.
 */
object Observability {

    private var reporter: CrashReporter = NoopCrashReporter
    private var initialized = false

    /**
     * Convenience entry point for platform startup code: builds the [ObservabilityConfig] from the
     * build-time [BuildKonfig] (DSN/environment) plus the two values only the platform knows:
     * [isDebug] (Android `BuildConfig.DEBUG`, iOS `Platform.isDebugBinary`) and [appVersion]
     * (Android `BuildConfig.VERSION_NAME`, iOS `CFBundleShortVersionString`).
     */
    fun init(isDebug: Boolean, appVersion: String) = init(
        ObservabilityConfig(
            dsn = BuildKonfig.SENTRY_DSN,
            environment = BuildKonfig.SENTRY_ENVIRONMENT,
            appVersion = appVersion,
            isDebug = isDebug,
        ),
    )

    fun init(config: ObservabilityConfig) {
        // Idempotent: startup paths can fire more than once (e.g. iOS re-entry), and we must not
        // re-initialize Sentry or re-stack log writers.
        if (initialized) return
        initialized = true

        // Crash reporting is opt-in via the DSN. With no DSN (local/dev) we stay fully offline — no
        // crash data leaves the device — which matches the BRD's offline-first, no-telemetry posture.
        reporter = if (config.dsn.isNotBlank()) {
            initSentry(config)
            SentryCrashReporter()
        } else {
            NoopCrashReporter
        }

        // BRD: tag every crash with build type, OS/platform and app version so reports can be
        // filtered and grouped by the build they came from.
        reporter.setTag("build_type", if (config.isDebug) "debug" else "release")
        reporter.setTag("platform", observabilityPlatform)
        reporter.setTag("app_version", config.appVersion)

        // BRD severity policy: debug builds log Verbose+, release builds log Info+.
        Logger.setMinSeverity(if (config.isDebug) Severity.Verbose else Severity.Info)
        Logger.setTag("LifeCompanion")

        // Install ONE writer that scrubs PII once and then fans out to the real sinks. Doing the
        // redaction here (rather than inside each sink) guarantees no sink — Logcat/os_log OR Sentry
        // — can ever see an unredacted message. Redaction runs only in release builds so developers
        // still see full messages locally (BRD §11.1: release builds must redact amounts/PII).
        Logger.setLogWriters(
            PiiScrubbingLogWriter(
                redactEnabled = !config.isDebug,
                delegates = listOf(
                    platformLogWriter(),
                    CrashReportingLogWriter(reporter),
                ),
            ),
        )
    }

    /** The active crash reporter. Returns a no-op until [init] has run with a configured DSN. */
    fun crashReporter(): CrashReporter = reporter

    private fun initSentry(config: ObservabilityConfig) {
        Sentry.init { options ->
            options.dsn = config.dsn
            options.environment = config.environment
            // Sentry calls the build identifier "release"; we feed it the app version.
            options.release = config.appVersion
            options.debug = config.isDebug
            // Last-chance PII scrub before anything is transmitted off-device. Unlike the log
            // writers — which redact in release builds only, since debug logs never leave the
            // device — this ALWAYS runs: any payload sent to Sentry must be scrubbed regardless of
            // build type (BRD §11.1/§11.2: no PII or financial amounts leave the device). We redact
            // the event message and each exception's message text (.value); stack frames are not in
            // SentryException, so crashes stay fully debuggable.
            options.beforeSend = { event ->
                event.message?.let { message ->
                    message.message = message.message?.let(::redact)
                    message.formatted = message.formatted?.let(::redact)
                }
                if (event.exceptions.isNotEmpty()) {
                    event.exceptions = event.exceptions
                        .map { exception -> exception.copy(value = exception.value?.let(::redact)) }
                        .toMutableList()
                }
                event
            }
        }
    }
}
