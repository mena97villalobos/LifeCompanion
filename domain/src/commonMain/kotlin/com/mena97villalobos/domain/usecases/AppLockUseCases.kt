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
