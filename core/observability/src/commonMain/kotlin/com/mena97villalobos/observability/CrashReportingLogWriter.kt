package com.mena97villalobos.observability

import co.touchlab.kermit.LogWriter
import co.touchlab.kermit.Severity

/**
 * A [LogWriter] that forwards `Error`/`Assert` level Kermit logs to the [CrashReporter] so that
 * error logging and crash reporting share a single call site. Messages reaching this writer have
 * already been PII-scrubbed by [PiiScrubbingLogWriter].
 */
internal class CrashReportingLogWriter(
    private val crashReporter: CrashReporter,
) : LogWriter() {

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
