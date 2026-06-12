package com.mena97villalobos.lifecompanion.ui.settings.passphrase

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mena97villalobos.domain.usecases.Passphrase
import com.mena97villalobos.lifecompanion.ui.components.PassphrasePrompt
import org.koin.compose.viewmodel.koinViewModel

/**
 * Settings section for the optional sensitive-operations passphrase (issue #9): a toggle to enable
 * or disable the requirement, a "Change passphrase" action, and the dialogs that gate each.
 */
@Composable
fun PassphraseSettingsSection(
    modifier: Modifier = Modifier,
    viewModel: PassphraseSettingsViewModel = koinViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column {
                Text("Require passphrase for sensitive operations")
                Text("Extra confirmation for destructive actions (${Passphrase.MIN_LENGTH}+ characters)")
            }
            Switch(
                checked = state.enabled,
                onCheckedChange = viewModel::onToggle,
                enabled = !state.isBusy,
            )
        }

        if (state.enabled) {
            TextButton(onClick = viewModel::onRequestChange, enabled = !state.isBusy) {
                Text("Change passphrase")
            }
        }

        state.message?.let { Text(it) }
    }

    when (val dialog = state.dialog) {
        is PassphraseDialog.Enable -> EnablePassphraseDialog(dialog, state.isBusy, viewModel)
        is PassphraseDialog.Disable -> PassphrasePrompt(
            title = "Disable passphrase",
            value = dialog.current,
            onValueChange = viewModel::onDisableCurrentChange,
            onConfirm = viewModel::submitDisable,
            onDismiss = viewModel::dismissDialog,
            error = dialog.error,
            lockoutRemainingSeconds = dialog.lockoutSeconds,
            isVerifying = state.isBusy,
            confirmLabel = "Disable",
        )
        is PassphraseDialog.Change -> ChangePassphraseDialog(dialog, state.isBusy, viewModel)
        null -> Unit
    }
}

@Composable
private fun EnablePassphraseDialog(
    dialog: PassphraseDialog.Enable,
    isBusy: Boolean,
    viewModel: PassphraseSettingsViewModel,
) {
    AlertDialog(
        onDismissRequest = viewModel::dismissDialog,
        title = { Text("Set passphrase") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                MaskedField("New passphrase", dialog.passphrase, viewModel::onEnablePassphraseChange, isBusy)
                MaskedField("Confirm passphrase", dialog.confirm, viewModel::onEnableConfirmChange, isBusy)
                dialog.error?.let { Text(it) }
            }
        },
        confirmButton = {
            TextButton(onClick = viewModel::submitEnable, enabled = !isBusy) { Text("Enable") }
        },
        dismissButton = {
            TextButton(onClick = viewModel::dismissDialog, enabled = !isBusy) { Text("Cancel") }
        },
    )
}

@Composable
private fun ChangePassphraseDialog(
    dialog: PassphraseDialog.Change,
    isBusy: Boolean,
    viewModel: PassphraseSettingsViewModel,
) {
    val lockedOut = dialog.lockoutSeconds > 0L
    AlertDialog(
        onDismissRequest = viewModel::dismissDialog,
        title = { Text("Change passphrase") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                MaskedField("Current passphrase", dialog.current, viewModel::onChangeCurrentChange, isBusy || lockedOut)
                MaskedField("New passphrase", dialog.newPassphrase, viewModel::onChangeNewChange, isBusy || lockedOut)
                MaskedField("Confirm new passphrase", dialog.confirm, viewModel::onChangeConfirmChange, isBusy || lockedOut)
                if (lockedOut) Text("Too many attempts. Try again in ${dialog.lockoutSeconds}s.")
                dialog.error?.let { Text(it) }
            }
        },
        confirmButton = {
            TextButton(onClick = viewModel::submitChange, enabled = !isBusy && !lockedOut) { Text("Update") }
        },
        dismissButton = {
            TextButton(onClick = viewModel::dismissDialog, enabled = !isBusy) { Text("Cancel") }
        },
    )
}

@Composable
private fun MaskedField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    disabled: Boolean,
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        singleLine = true,
        enabled = !disabled,
        visualTransformation = PasswordVisualTransformation(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        modifier = Modifier.fillMaxWidth(),
    )
}
