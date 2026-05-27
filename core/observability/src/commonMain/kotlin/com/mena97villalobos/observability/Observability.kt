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

    /**
     * Convenience entry point for platform startup code: builds the [ObservabilityConfig] from the
     * generated [BuildKonfig] (DSN/environment/version) and the supplied [isDebug] flag, which only
     * the platform knows (Android `BuildConfig.DEBUG`, iOS `Platform.isDebugBinary`).
     */
    fun init(isDebug: Boolean) = init(
        ObservabilityConfig(
            dsn = BuildKonfig.SENTRY_DSN,
            environment = BuildKonfig.SENTRY_ENVIRONMENT,
            release = BuildKonfig.APP_VERSION,
            isDebug = isDebug,
        ),
    )

    fun init(config: ObservabilityConfig) {
        reporter = if (config.dsn.isNotBlank()) {
            initSentry(config)
            SentryCrashReporter()
        } else {
            NoopCrashReporter
        }

        reporter.setTag("build_type", if (config.isDebug) "debug" else "release")
        reporter.setTag("platform", observabilityPlatform)
        reporter.setTag("app_version", config.release)

        Logger.setMinSeverity(if (config.isDebug) Severity.Verbose else Severity.Info)
        Logger.setTag("LifeCompanion")
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
            options.release = config.release
            options.debug = config.isDebug
        }
    }
}
