package com.mena97villalobos.lifecompanion.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mena97villalobos.domain.security.BiometricAuthenticator
import com.mena97villalobos.domain.security.BiometricAvailability
import com.mena97villalobos.domain.security.BiometricResult
import com.mena97villalobos.domain.usecases.ObserveBiometricEnabledUseCase
import com.mena97villalobos.domain.usecases.SetBiometricEnabledUseCase
import com.mena97villalobos.domain.usecases.SetupPinUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class SettingsUiState(
    val biometricEnabled: Boolean = false,
    val biometricAvailable: Boolean = false,
    val changePinValue: String = "",
    val confirmPinValue: String = "",
    val message: String? = null,
) {
    val canSavePin: Boolean
        get() = SetupPinUseCase.isValidPin(changePinValue) && changePinValue == confirmPinValue
}

/**
 * Settings for the app lock (issue #7): toggle biometric unlock on/off and change the PIN. Enabling
 * biometric first confirms the user can pass the system prompt so we never enable a method that does
 * not work on this device.
 */
class SettingsViewModel(
    observeBiometricEnabled: ObserveBiometricEnabledUseCase,
    private val setBiometricEnabled: SetBiometricEnabledUseCase,
    private val setupPin: SetupPinUseCase,
    private val biometricAuthenticator: BiometricAuthenticator,
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        SettingsUiState(
            biometricAvailable = biometricAuthenticator.availability() == BiometricAvailability.AVAILABLE,
        ),
    )
    val uiState: StateFlow<SettingsUiState> = _uiState

    init {
        viewModelScope.launch {
            observeBiometricEnabled().collect { enabled -> _uiState.update { it.copy(biometricEnabled = enabled) } }
        }
    }

    fun onBiometricToggle(enabled: Boolean) {
        if (!enabled) {
            viewModelScope.launch { setBiometricEnabled(false) }
            return
        }
        if (!_uiState.value.biometricAvailable) {
            _uiState.update { it.copy(message = "Biometric is not available on this device") }
            return
        }
        viewModelScope.launch {
            when (biometricAuthenticator.authenticate(title = "Confirm to enable biometric unlock")) {
                BiometricResult.Success -> {
                    setBiometricEnabled(true)
                    _uiState.update { it.copy(message = "Biometric unlock enabled") }
                }

                BiometricResult.Cancelled -> Unit
                is BiometricResult.Failed -> _uiState.update { it.copy(message = "Could not verify biometric") }
            }
        }
    }

    fun onChangePinValue(value: String) =
        _uiState.update { it.copy(changePinValue = sanitizePin(value), message = null) }

    fun onConfirmPinValue(value: String) =
        _uiState.update { it.copy(confirmPinValue = sanitizePin(value), message = null) }

    fun savePin() {
        val state = _uiState.value
        if (!state.canSavePin) {
            val message = "Enter a matching ${SetupPinUseCase.PIN_LENGTH}-digit PIN"
            _uiState.update { it.copy(message = message) }
            return
        }
        viewModelScope.launch {
            runCatching { setupPin(state.changePinValue) }
                .onSuccess { _uiState.update { it.copy(changePinValue = "", confirmPinValue = "", message = "PIN updated") } }
                .onFailure { error -> _uiState.update { it.copy(message = error.message ?: "Could not update PIN") } }
        }
    }

    fun consumeMessage() = _uiState.update { it.copy(message = null) }

    private fun sanitizePin(value: String): String =
        value.filter(Char::isDigit).take(SetupPinUseCase.PIN_LENGTH)
}
