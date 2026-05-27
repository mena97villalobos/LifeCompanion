package com.mena97villalobos.observability

/**
 * Runtime configuration for the observability layer.
 *
 * [dsn], [environment] and [appVersion] come from the build-time [BuildKonfig] (so the DSN is never
 * hardcoded in source). [isDebug] is supplied by each platform entry point because only the
 * platform knows its own build type (Android `BuildConfig.DEBUG`, iOS `Platform.isDebugBinary`).
 *
 * @property appVersion the app's version string; surfaced to Sentry as the "release" (the term
 * Sentry uses) so crashes can be grouped by the build they came from.
 */
data class ObservabilityConfig(
    val dsn: String,
    val environment: String,
    val appVersion: String,
    val isDebug: Boolean,
)
