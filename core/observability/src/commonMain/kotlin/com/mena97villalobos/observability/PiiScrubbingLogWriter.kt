package com.mena97villalobos.observability

import co.touchlab.kermit.LogWriter
import co.touchlab.kermit.Severity

/**
 * A [LogWriter] that scrubs PII from every message (in release builds) before fanning it out to the
 * real writers. Registering this single writer — rather than each sink independently — guarantees
 * that the redaction happens exactly once and that no sink (including the Sentry one) ever sees the
 * raw message.
 *
 * @param redactEnabled when true (release builds) messages are passed through [redact] first.
 * @param delegates the underlying sinks (e.g. the platform logcat/os_log writer and the crash
 * reporting writer).
 */
class PiiScrubbingLogWriter(
    private val redactEnabled: Boolean,
    private val delegates: List<LogWriter>,
) : LogWriter() {

    override fun isLoggable(tag: String, severity: Severity): Boolean =
        delegates.any { it.isLoggable(tag, severity) }

    override fun log(severity: Severity, message: String, tag: String, throwable: Throwable?) {
        val safeMessage = if (redactEnabled) redact(message) else message
        delegates.forEach { delegate ->
            if (delegate.isLoggable(tag, severity)) {
                delegate.log(severity, safeMessage, tag, throwable)
            }
        }
    }
}
