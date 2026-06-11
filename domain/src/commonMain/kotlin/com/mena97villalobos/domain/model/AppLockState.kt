package com.mena97villalobos.domain.model

/**
 * Runtime lock state of the app. The UI gates sensitive screens on this: only [Unlocked] renders
 * protected content; everything else shows the lock screen (or onboarding when no lock is set up).
 */
sealed interface AppLockState {
    /** No PIN has been configured yet — the app is effectively open (pre-onboarding completion). */
    data object NotConfigured : AppLockState

    /** A lock is configured and currently engaged; the user must authenticate to proceed. */
    data object Locked : AppLockState

    /**
     * Authentication is blocked by exponential backoff after repeated PIN failures.
     *
     * @param remainingMillis time left until the user may try the PIN again.
     * @param failedAttempts total consecutive failures recorded so far.
     */
    data class LockedOut(
        val remainingMillis: Long,
        val failedAttempts: Int,
    ) : AppLockState

    /** Authenticated; sensitive screens may render. */
    data object Unlocked : AppLockState
}

/** How the user can unlock the app. Persisted preferences decide which are offered. */
enum class LockMethod {
    PIN,
    BIOMETRIC,
}
