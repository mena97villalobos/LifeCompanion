package com.mena97villalobos.lifecompanion.ui.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mena97villalobos.domain.model.AppLocale
import com.mena97villalobos.domain.model.Currency
import com.mena97villalobos.domain.model.UserProfile
import com.mena97villalobos.domain.security.AppLockManager
import com.mena97villalobos.domain.security.BiometricAuthenticator
import com.mena97villalobos.domain.security.BiometricAvailability
import com.mena97villalobos.domain.usecases.SaveProfileUseCase
import com.mena97villalobos.domain.usecases.SetBiometricEnabledUseCase
import com.mena97villalobos.domain.usecases.SetupPinUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class OnboardingStep { WELCOME, PROFILE, APP_LOCK }

data class OnboardingUiState(
    val step: OnboardingStep = OnboardingStep.WELCOME,
    val displayName: String = "",
    val currency: Currency = Currency.Default,
    val locale: AppLocale = AppLocale.Default,
    val pin: String = "",
    val confirmPin: String = "",
    val biometricAvailable: Boolean = false,
    val enableBiometric: Boolean = false,
    val error: String? = null,
    val isSaving: Boolean = false,
) {
    val canContinueProfile: Boolean get() = displayName.isNotBlank()
    val canFinish: Boolean
        get() = SetupPinUseCase.isValidPin(pin) && pin == confirmPin && !isSaving
}

/**
 * Drives the first-launch onboarding wizard: welcome -> profile -> app-lock. On finish it persists
 * the [UserProfile], stores the Argon2id PIN hash, applies the biometric preference, and unlocks the
 * app so the user lands on the dashboard.
 */
class OnboardingViewModel(
    private val saveProfile: SaveProfileUseCase,
    private val setupPin: SetupPinUseCase,
    private val setBiometricEnabled: SetBiometricEnabledUseCase,
    private val appLockManager: AppLockManager,
    biometricAuthenticator: BiometricAuthenticator,
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        OnboardingUiState(
            biometricAvailable = biometricAuthenticator.availability() == BiometricAvailability.AVAILABLE,
        ),
    )
    val uiState: StateFlow<OnboardingUiState> = _uiState

    fun onDisplayNameChange(value: String) = _uiState.update { it.copy(displayName = value, error = null) }

    fun onCurrencySelected(currency: Currency) = _uiState.update { it.copy(currency = currency) }

    fun onLocaleSelected(locale: AppLocale) = _uiState.update { it.copy(locale = locale) }

    fun onPinChange(value: String) =
        _uiState.update { it.copy(pin = value.filter(Char::isDigit).take(PIN_LENGTH), error = null) }

    fun onConfirmPinChange(value: String) =
        _uiState.update { it.copy(confirmPin = value.filter(Char::isDigit).take(PIN_LENGTH), error = null) }

    fun onBiometricToggle(enabled: Boolean) = _uiState.update { it.copy(enableBiometric = enabled) }

    fun next() = _uiState.update {
        val nextStep = when (it.step) {
            OnboardingStep.WELCOME -> OnboardingStep.PROFILE
            OnboardingStep.PROFILE -> OnboardingStep.APP_LOCK
            OnboardingStep.APP_LOCK -> OnboardingStep.APP_LOCK
        }
        it.copy(step = nextStep, error = null)
    }

    fun back() = _uiState.update {
        val previous = when (it.step) {
            OnboardingStep.WELCOME -> OnboardingStep.WELCOME
            OnboardingStep.PROFILE -> OnboardingStep.WELCOME
            OnboardingStep.APP_LOCK -> OnboardingStep.PROFILE
        }
        it.copy(step = previous, error = null)
    }

    fun finish() {
        val current = _uiState.value
        if (!current.canFinish) {
            _uiState.update { it.copy(error = "Enter a matching $PIN_LENGTH-digit PIN") }
            return
        }
        _uiState.update { it.copy(isSaving = true, error = null) }
        viewModelScope.launch {
            runCatching {
                saveProfile(
                    UserProfile(
                        displayName = current.displayName.trim(),
                        currency = current.currency,
                        locale = current.locale,
                    ),
                )
                setupPin(current.pin)
                setBiometricEnabled(current.enableBiometric && current.biometricAvailable)
            }.onSuccess {
                appLockManager.markUnlocked()
            }.onFailure { error ->
                _uiState.update { it.copy(isSaving = false, error = error.message ?: "Could not complete setup") }
            }
        }
    }

    private companion object {
        const val PIN_LENGTH = SetupPinUseCase.PIN_LENGTH
    }
}
