package com.mena97villalobos.observability

/**
 * Runtime configuration for the observability layer.
 *
 * [dsn], [environment] and [release] come from the build-time [BuildKonfig] (so the DSN is never
 * hardcoded in source). [isDebug] is supplied by each platform entry point because only the
 * platform knows its own build type (Android `BuildConfig.DEBUG`, iOS `Platform.isDebugBinary`).
 */
data class ObservabilityConfig(
    val dsn: String,
    val environment: String,
    val release: String,
    val isDebug: Boolean,
)
