package com.mena97villalobos.domain.security

import com.mena97villalobos.domain.repository.PassphraseRepository
import kotlin.time.Clock

/** Result of a passphrase confirmation attempt, surfaced to the prompt UI. */
sealed interface PassphraseOutcome {
    data object Success : PassphraseOutcome

    /**
     * Wrong passphrase. [remainingAttemptsBeforeLockout] is null once backoff has already engaged.
     */
    data class Failed(val remainingAttemptsBeforeLockout: Int?) : PassphraseOutcome

    /** Attempt rejected because backoff is active; retry after [remainingMillis]. */
    data class LockedOut(val remainingMillis: Long) : PassphraseOutcome
}

/**
 * Verifies the sensitive-operations passphrase (issue #9) and applies the **same exponential backoff
 * as the PIN** (see [lockoutDurationMillis] / [BackoffConfig]) on repeated failures.
 *
 * Unlike [AppLockManager] this holds no persistent lock state: a passphrase prompt is a transient
 * gate in front of a single [SensitiveOperation], so callers drive it per-attempt via [submit] and
 * read [remainingLockoutMillis] to initialise the prompt. The class is side-effect-free beyond the
 * injected [PassphraseRepository], so it is fully unit-testable with a fake repository and a
 * deterministic [now] clock.
 */
class PassphraseManager(
    private val repository: PassphraseRepository,
    private val config: BackoffConfig = BackoffConfig(),
    private val now: () -> Long = { Clock.System.now().toEpochMilliseconds() },
) {
    /**
     * Validates [passphrase]. On success resets backoff; on failure records the attempt and may
     * report [PassphraseOutcome.LockedOut]. Rejects immediately while backoff is active.
     */
    suspend fun submit(passphrase: String): PassphraseOutcome {
        val remaining = remainingLockoutMillis()
        if (remaining > 0) return PassphraseOutcome.LockedOut(remaining)

        return if (repository.verifyPassphrase(passphrase)) {
            repository.resetFailedAttempts()
            PassphraseOutcome.Success
        } else {
            repository.recordFailedAttempt(now())
            val duration = remainingLockoutMillis()
            if (duration > 0) {
                PassphraseOutcome.LockedOut(duration)
            } else {
                PassphraseOutcome.Failed(remainingAttemptsBeforeLockout())
            }
        }
    }

    /** Remaining backoff lockout in millis right now (0 when the user may try immediately). */
    suspend fun remainingLockoutMillis(): Long {
        val failures = repository.getFailedAttempts()
        val duration = lockoutDurationMillis(failures, config)
        if (duration <= 0L) return 0L
        val lastFailedAt = repository.getLastFailedAtMillis() ?: return 0L
        return (lastFailedAt + duration - now()).coerceAtLeast(0L)
    }

    private suspend fun remainingAttemptsBeforeLockout(): Int? {
        val failures = repository.getFailedAttempts()
        val left = config.firstLockoutAt - failures
        return if (left > 0) left else null
    }
}
