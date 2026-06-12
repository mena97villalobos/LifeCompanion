package com.mena97villalobos.lifecompanion.ui.lock

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mena97villalobos.domain.model.AppLockState
import com.mena97villalobos.domain.security.AppLockManager
import com.mena97villalobos.domain.security.BiometricAuthenticator
import com.mena97villalobos.domain.security.BiometricAvailability
import com.mena97villalobos.domain.security.BiometricResult
import com.mena97villalobos.domain.security.UnlockOutcome
import com.mena97villalobos.domain.usecases.ObserveBiometricEnabledUseCase
import com.mena97villalobos.domain.usecases.SetupPinUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class LockUiState(
    val pin: String = "",
    val error: String? = null,
    val lockState: AppLockState = AppLockState.Locked,
    val biometricEnabled: Boolean = false,
    val biometricAvailable: Boolean = false,
    val isVerifying: Boolean = false,
) {
    val isLockedOut: Boolean get() = lockState is AppLockState.LockedOut
    val lockoutRemainingMillis: Long get() = (lockState as? AppLockState.LockedOut)?.remainingMillis ?: 0L
    val canOfferBiometric: Boolean get() = biometricEnabled && biometricAvailable && !isLockedOut
}

/**
 * Drives the lock screen: PIN entry (with exponential backoff surfaced from [AppLockManager]) and an
 * optional biometric unlock when the user has enabled it.
 */
class LockViewModel(
    private val appLockManager: AppLockManager,
    private val observeBiometricEnabled: ObserveBiometricEnabledUseCase,
    private val biometricAuthenticator: BiometricAuthenticator,
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        LockUiState(
            biometricAvailable = biometricAuthenticator.availability() == BiometricAvailability.AVAILABLE,
        ),
    )
    val uiState: StateFlow<LockUiState> = _uiState

    init {
        viewModelScope.launch {
            appLockManager.state.collect { state -> _uiState.update { it.copy(lockState = state) } }
        }
        viewModelScope.launch {
            observeBiometricEnabled().collect { enabled -> _uiState.update { it.copy(biometricEnabled = enabled) } }
        }
    }

    fun onPinChange(value: String) =
        _uiState.update { it.copy(pin = value.filter(Char::isDigit).take(SetupPinUseCase.PIN_LENGTH), error = null) }

    fun submitPin() {
        val pin = _uiState.value.pin
        if (pin.isEmpty()) return
        _uiState.update { it.copy(isVerifying = true) }
        viewModelScope.launch {
            val outcome = appLockManager.submitPin(pin)
            _uiState.update {
                it.copy(
                    isVerifying = false,
                    pin = "",
                    error = outcome.toErrorMessage(),
                )
            }
        }
    }

    /** Refreshes the remaining lockout time; call on a UI tick while locked out. */
    fun refreshLockout() {
        viewModelScope.launch { appLockManager.refreshLockout() }
    }

    fun authenticateWithBiometric() {
        if (!_uiState.value.canOfferBiometric) return
        viewModelScope.launch {
            when (val result = biometricAuthenticator.authenticate(title = "Unlock LifeCompanion")) {
                BiometricResult.Success -> appLockManager.markUnlocked()
                BiometricResult.Cancelled -> Unit
                is BiometricResult.Failed -> _uiState.update { it.copy(error = result.message ?: "Biometric failed") }
            }
        }
    }

    private fun UnlockOutcome.toErrorMessage(): String? = when (this) {
        UnlockOutcome.Success -> null

        is UnlockOutcome.Failed ->
            remainingAttemptsBeforeLockout
                ?.let { "Incorrect PIN. $it attempt(s) left." }
                ?: "Incorrect PIN."

        is UnlockOutcome.LockedOut -> "Too many attempts. Try again soon."
    }
}
