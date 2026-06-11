package com.mena97villalobos.observability

import co.touchlab.kermit.LogWriter
import co.touchlab.kermit.Severity

/**
 * A [LogWriter] that forwards `Error`/`Assert` level Kermit logs to the [CrashReporter] so that
 * error logging and crash reporting share a single call site: logging an error is reporting it.
 *
 * PII protection here is two-layered. The [message] string arrives already scrubbed (release builds)
 * from [PiiScrubbingLogWriter]. The [throwable] is passed raw — its message could still contain PII —
 * so the final scrub for anything transmitted happens in Sentry's `beforeSend` hook (see
 * [Observability]), which always runs regardless of build type.
 */
internal class CrashReportingLogWriter(
    private val crashReporter: CrashReporter,
) : LogWriter() {

    // Only Error/Assert reach Sentry; lower severities stay on the local sinks.
    override fun isLoggable(tag: String, severity: Severity): Boolean =
        severity.ordinal >= Severity.Error.ordinal

    override fun log(severity: Severity, message: String, tag: String, throwable: Throwable?) {
        if (throwable != null) {
            crashReporter.captureException(throwable)
        } else {
            crashReporter.captureMessage("[$tag] $message")
        }
    }
}
