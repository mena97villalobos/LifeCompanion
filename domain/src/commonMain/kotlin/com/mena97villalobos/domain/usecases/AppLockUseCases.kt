package com.mena97villalobos.domain.usecases

import com.mena97villalobos.domain.repository.AppLockRepository
import kotlinx.coroutines.flow.Flow

/**
 * Configures the PIN during onboarding or from settings. The [pin] is hashed with Argon2id inside
 * the repository before storage and validated here to be a 6-digit numeric PIN.
 */
class SetupPinUseCase(
    private val repository: AppLockRepository,
) {
    suspend operator fun invoke(pin: String) {
        require(isValidPin(pin)) { "PIN must be exactly $PIN_LENGTH digits" }
        repository.setPin(pin)
    }

    companion object {
        const val PIN_LENGTH = 6

        fun isValidPin(pin: String): Boolean =
            pin.length == PIN_LENGTH && pin.all { it.isDigit() }
    }
}

/** Toggles biometric unlock on or off. */
class SetBiometricEnabledUseCase(
    private val repository: AppLockRepository,
) {
    suspend operator fun invoke(enabled: Boolean) = repository.setBiometricEnabled(enabled)
}

/** Observes whether biometric unlock is enabled, for the settings toggle. */
class ObserveBiometricEnabledUseCase(
    private val repository: AppLockRepository,
) {
    operator fun invoke(): Flow<Boolean> = repository.observeBiometricEnabled()
}

/** True once a PIN has been configured (used to decide whether app-lock setup is complete). */
class IsPinSetUseCase(
    private val repository: AppLockRepository,
) {
    suspend operator fun invoke(): Boolean = repository.isPinSet()
}

/** Reads the current inactivity auto-lock timeout, expressed in whole minutes for the UI. */
class GetInactivityTimeoutUseCase(
    private val repository: AppLockRepository,
) {
    suspend operator fun invoke(): Int =
        (repository.getInactivityTimeoutMillis() / 60_000L).toInt()
            .coerceIn(InactivityTimeout.MIN_MINUTES, InactivityTimeout.MAX_MINUTES)
}

/** Persists the inactivity auto-lock timeout (minutes), clamped to the allowed range. */
class SetInactivityTimeoutUseCase(
    private val repository: AppLockRepository,
) {
    suspend operator fun invoke(minutes: Int) {
        val clamped = minutes.coerceIn(InactivityTimeout.MIN_MINUTES, InactivityTimeout.MAX_MINUTES)
        repository.setInactivityTimeoutMillis(clamped * 60_000L)
    }
}

/** Bounds for the user-configurable inactivity timeout (issue #8: 1–30 minutes, default 5). */
object InactivityTimeout {
    const val MIN_MINUTES = 1
    const val MAX_MINUTES = 30
    const val DEFAULT_MINUTES = 5
}
