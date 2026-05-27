package com.mena97villalobos.observability

import io.sentry.kotlin.multiplatform.Sentry

/**
 * Platform-agnostic crash/error reporting facade. Backed by Sentry KMP at runtime, but consumers
 * depend only on this interface so the reporting backend can be swapped without touching call sites.
 */
interface CrashReporter {
    /** Report a caught or uncaught throwable as a Sentry event. */
    fun captureException(throwable: Throwable)

    /** Report a standalone message (used for high-severity logs without a throwable). */
    fun captureMessage(message: String)

    /** Attach a tag to all subsequent events (e.g. build type, platform, app version). */
    fun setTag(key: String, value: String)
}

/** Crash reporter that drops everything — used when no Sentry DSN is configured (local/dev). */
internal object NoopCrashReporter : CrashReporter {
    override fun captureException(throwable: Throwable) = Unit
    override fun captureMessage(message: String) = Unit
    override fun setTag(key: String, value: String) = Unit
}

/** [CrashReporter] backed by the shared Sentry KMP SDK. */
internal class SentryCrashReporter : CrashReporter {
    override fun captureException(throwable: Throwable) {
        Sentry.captureException(throwable)
    }

    override fun captureMessage(message: String) {
        Sentry.captureMessage(message)
    }

    override fun setTag(key: String, value: String) {
        Sentry.configureScope { scope -> scope.setTag(key, value) }
    }
}
