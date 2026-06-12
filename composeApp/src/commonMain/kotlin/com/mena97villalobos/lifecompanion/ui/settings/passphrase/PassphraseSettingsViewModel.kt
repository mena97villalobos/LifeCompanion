package com.mena97villalobos.lifecompanion.ui.settings.passphrase

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mena97villalobos.domain.security.PassphraseOutcome
import com.mena97villalobos.domain.usecases.ChangePassphraseUseCase
import com.mena97villalobos.domain.usecases.DisablePassphraseUseCase
import com.mena97villalobos.domain.usecases.EnablePassphraseUseCase
import com.mena97villalobos.domain.usecases.ObservePassphraseEnabledUseCase
import com.mena97villalobos.domain.usecases.Passphrase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/** Which passphrase dialog is open, with its own field/error state. */
sealed interface PassphraseDialog {
    /** Set a new passphrase to enable the requirement (no current passphrase yet). */
    data class Enable(
        val passphrase: String = "",
        val confirm: String = "",
        val error: String? = null,
    ) : PassphraseDialog

    /** Confirm the current passphrase to disable the requirement. */
    data class Disable(
        val current: String = "",
        val error: String? = null,
        val lockoutSeconds: Long = 0L,
    ) : PassphraseDialog

    /** Confirm the current passphrase and set a new one. */
    data class Change(
        val current: String = "",
        val newPassphrase: String = "",
        val confirm: String = "",
        val error: String? = null,
        val lockoutSeconds: Long = 0L,
    ) : PassphraseDialog
}

data class PassphraseSettingsUiState(
    val enabled: Boolean = false,
    val isBusy: Boolean = false,
    val dialog: PassphraseDialog? = null,
    val message: String? = null,
)

/**
 * Settings for the optional sensitive-operations passphrase (issue #9): enable it by setting a
 * passphrase, disable it by confirming the current one, or change it. Enabling/disabling/changing
 * are themselves gated, so disabling and changing require the current passphrase and share the PIN's
 * exponential backoff via [DisablePassphraseUseCase] / [ChangePassphraseUseCase].
 */
class PassphraseSettingsViewModel(
    observePassphraseEnabled: ObservePassphraseEnabledUseCase,
    private val enablePassphrase: EnablePassphraseUseCase,
    private val disablePassphrase: DisablePassphraseUseCase,
    private val changePassphrase: ChangePassphraseUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(PassphraseSettingsUiState())
    val uiState: StateFlow<PassphraseSettingsUiState> = _uiState

    init {
        viewModelScope.launch {
            observePassphraseEnabled().collect { enabled -> _uiState.update { it.copy(enabled = enabled) } }
        }
    }

    fun onToggle(enable: Boolean) {
        if (enable == _uiState.value.enabled) return
        val dialog = if (enable) PassphraseDialog.Enable() else PassphraseDialog.Disable()
        _uiState.update { it.copy(dialog = dialog, message = null) }
    }

    fun onRequestChange() {
        _uiState.update { it.copy(dialog = PassphraseDialog.Change(), message = null) }
    }

    fun dismissDialog() {
        _uiState.update { it.copy(dialog = null) }
    }

    fun consumeMessage() = _uiState.update { it.copy(message = null) }

    // --- Enable ---------------------------------------------------------------------------------

    fun onEnablePassphraseChange(value: String) = updateEnable { it.copy(passphrase = value, error = null) }

    fun onEnableConfirmChange(value: String) = updateEnable { it.copy(confirm = value, error = null) }

    fun submitEnable() {
        val dialog = _uiState.value.dialog as? PassphraseDialog.Enable ?: return
        when {
            !Passphrase.isValid(dialog.passphrase) ->
                updateEnable { it.copy(error = "Use at least ${Passphrase.MIN_LENGTH} characters") }

            dialog.passphrase != dialog.confirm ->
                updateEnable { it.copy(error = "Passphrases do not match") }

            else -> withBusy {
                runCatching { enablePassphrase(dialog.passphrase) }
                    .onSuccess { closeWith("Passphrase requirement enabled") }
                    .onFailure { e -> updateEnable { it.copy(error = e.message ?: "Could not enable") } }
            }
        }
    }

    // --- Disable --------------------------------------------------------------------------------

    fun onDisableCurrentChange(value: String) = updateDisable { it.copy(current = value, error = null) }

    fun submitDisable() {
        val dialog = _uiState.value.dialog as? PassphraseDialog.Disable ?: return
        if (dialog.current.isEmpty()) return
        withBusy {
            when (val outcome = disablePassphrase(dialog.current)) {
                PassphraseOutcome.Success -> closeWith("Passphrase requirement disabled")
                is PassphraseOutcome.Failed -> updateDisable { it.copy(error = outcome.failedMessage(), lockoutSeconds = 0L) }
                is PassphraseOutcome.LockedOut -> updateDisable { it.copy(error = null, lockoutSeconds = outcome.seconds()) }
            }
        }
    }

    // --- Change ---------------------------------------------------------------------------------

    fun onChangeCurrentChange(value: String) = updateChange { it.copy(current = value, error = null) }

    fun onChangeNewChange(value: String) = updateChange { it.copy(newPassphrase = value, error = null) }

    fun onChangeConfirmChange(value: String) = updateChange { it.copy(confirm = value, error = null) }

    fun submitChange() {
        val dialog = _uiState.value.dialog as? PassphraseDialog.Change ?: return
        when {
            dialog.current.isEmpty() -> updateChange { it.copy(error = "Enter your current passphrase") }

            !Passphrase.isValid(dialog.newPassphrase) ->
                updateChange { it.copy(error = "New passphrase needs at least ${Passphrase.MIN_LENGTH} characters") }

            dialog.newPassphrase != dialog.confirm ->
                updateChange { it.copy(error = "New passphrases do not match") }

            else -> withBusy {
                when (val outcome = changePassphrase(dialog.current, dialog.newPassphrase)) {
                    PassphraseOutcome.Success -> closeWith("Passphrase changed")
                    is PassphraseOutcome.Failed -> updateChange { it.copy(error = outcome.failedMessage(), lockoutSeconds = 0L) }
                    is PassphraseOutcome.LockedOut -> updateChange { it.copy(error = null, lockoutSeconds = outcome.seconds()) }
                }
            }
        }
    }

    // --- Helpers --------------------------------------------------------------------------------

    private fun withBusy(block: suspend () -> Unit) {
        _uiState.update { it.copy(isBusy = true) }
        viewModelScope.launch {
            block()
            _uiState.update { it.copy(isBusy = false) }
        }
    }

    private fun closeWith(message: String) = _uiState.update { it.copy(dialog = null, message = message) }

    private fun updateEnable(transform: (PassphraseDialog.Enable) -> PassphraseDialog.Enable) =
        _uiState.update { state ->
            (state.dialog as? PassphraseDialog.Enable)?.let { state.copy(dialog = transform(it)) } ?: state
        }

    private fun updateDisable(transform: (PassphraseDialog.Disable) -> PassphraseDialog.Disable) =
        _uiState.update { state ->
            (state.dialog as? PassphraseDialog.Disable)?.let { state.copy(dialog = transform(it)) } ?: state
        }

    private fun updateChange(transform: (PassphraseDialog.Change) -> PassphraseDialog.Change) =
        _uiState.update { state ->
            (state.dialog as? PassphraseDialog.Change)?.let { state.copy(dialog = transform(it)) } ?: state
        }

    private fun PassphraseOutcome.Failed.failedMessage(): String =
        remainingAttemptsBeforeLockout
            ?.let { "Incorrect passphrase. $it attempt(s) left." }
            ?: "Incorrect passphrase."

    private fun PassphraseOutcome.LockedOut.seconds(): Long = (remainingMillis + 999L) / 1_000L
}
