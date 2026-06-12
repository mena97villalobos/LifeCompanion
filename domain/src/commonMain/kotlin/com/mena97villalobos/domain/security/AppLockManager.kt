package com.mena97villalobos.domain.security

import com.mena97villalobos.domain.model.AppLockState
import com.mena97villalobos.domain.repository.AppLockRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.time.Clock

/** Result of a PIN unlock attempt, surfaced to the UI. */
sealed interface UnlockOutcome {
    data object Success : UnlockOutcome

    /** Wrong PIN. [remainingAttemptsBeforeLockout] is null once backoff has already engaged. */
    data class Failed(val remainingAttemptsBeforeLockout: Int?) : UnlockOutcome

    /** Attempt rejected because backoff is active; retry after [remainingMillis]. */
    data class LockedOut(val remainingMillis: Long) : UnlockOutcome
}

/**
 * Holds the runtime [AppLockState] and arbitrates PIN/biometric unlocks, applying exponential
 * backoff (see [lockoutDurationMillis]) on repeated PIN failures.
 *
 * This class is platform-agnostic and side-effect-free beyond the injected [AppLockRepository], so
 * it is fully unit-testable: inject a fake repository and a deterministic [now] clock.
 *
 * The runtime inactivity/auto-lock *timer* is owned by the UI layer (a later ticket); this manager
 * provides [lock]/[markUnlocked]/[submitPin] for it to drive.
 */
class AppLockManager(
    private val repository: AppLockRepository,
    private val config: BackoffConfig = BackoffConfig(),
    private val now: () -> Long = { Clock.System.now().toEpochMilliseconds() },
) {
    private val _state = MutableStateFlow<AppLockState>(AppLockState.NotConfigured)
    val state: StateFlow<AppLockState> = _state.asStateFlow()

    /** Computes the initial state on launch: locked when a PIN exists, otherwise not configured. */
    suspend fun initialize() {
        _state.value = if (repository.isPinSet()) lockedState() else AppLockState.NotConfigured
    }

    /** Re-engages the lock (manual lock, backgrounding, or inactivity timeout). */
    suspend fun lock() {
        _state.value = if (repository.isPinSet()) lockedState() else AppLockState.NotConfigured
    }

    /** Marks the app unlocked after a successful biometric prompt. */
    fun markUnlocked() {
        _state.value = AppLockState.Unlocked
    }

    /** Recomputes [AppLockState.LockedOut] remaining time; call this on a UI tick while locked out. */
    suspend fun refreshLockout() {
        if (_state.value is AppLockState.LockedOut) {
            _state.value = lockedState()
        }
    }

    /**
     * Validates [pin]. On success resets backoff and unlocks; on failure records the attempt and
     * may transition to [AppLockState.LockedOut]. Rejects immediately while backoff is active.
     */
    suspend fun submitPin(pin: String): UnlockOutcome {
        val remaining = remainingLockoutMillis()
        if (remaining > 0) {
            _state.value = AppLockState.LockedOut(remaining, repository.getFailedAttempts())
            return UnlockOutcome.LockedOut(remaining)
        }

        return if (repository.verifyPin(pin)) {
            repository.resetFailedAttempts()
            markUnlocked()
            UnlockOutcome.Success
        } else {
            repository.recordFailedAttempt(now())
            val state = lockedState()
            _state.value = state
            when (state) {
                is AppLockState.LockedOut -> UnlockOutcome.LockedOut(state.remainingMillis)
                else -> UnlockOutcome.Failed(remainingAttemptsBeforeLockout())
            }
        }
    }

    private suspend fun lockedState(): AppLockState {
        val remaining = remainingLockoutMillis()
        return if (remaining > 0) {
            AppLockState.LockedOut(remaining, repository.getFailedAttempts())
        } else {
            AppLockState.Locked
        }
    }

    private suspend fun remainingLockoutMillis(): Long {
        val failures = repository.getFailedAttempts()
        val duration = lockoutDurationMillis(failures, config)
        if (duration <= 0L) return 0L
        val lastFailedAt = repository.getLastFailedAtMillis() ?: return 0L
        return (lastFailedAt + duration - now()).coerceAtLeast(0L)
    }

    private suspend fun remainingAttemptsBeforeLockout(): Int? {
        val failures = repository.getFailedAttempts()
        val left = config.failureThreshold - failures
        return if (left > 0) left else null
    }
}
