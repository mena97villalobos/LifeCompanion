package com.mena97villalobos.lifecompanion.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp

/**
 * Reusable confirmation dialog with masked input for the sensitive-operations passphrase (issue #9).
 *
 * Any feature that performs a [com.mena97villalobos.domain.security.SensitiveOperation] while the
 * requirement is enabled shows this prompt and only proceeds on a
 * [com.mena97villalobos.domain.security.PassphraseOutcome.Success] reported back through [error] /
 * the caller's state.
 *
 * @param title heading describing what is being confirmed (e.g. "Disable passphrase").
 * @param value current masked input.
 * @param error message to show under the field (wrong passphrase / lockout), or null.
 * @param lockoutRemainingSeconds seconds left of backoff lockout; when > 0 input is blocked.
 * @param isVerifying true while a submission is in flight.
 * @param confirmLabel label for the confirm button (defaults to "Confirm").
 */
@Composable
fun PassphrasePrompt(
    title: String,
    value: String,
    onValueChange: (String) -> Unit,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    error: String? = null,
    lockoutRemainingSeconds: Long = 0L,
    isVerifying: Boolean = false,
    confirmLabel: String = "Confirm",
) {
    val lockedOut = lockoutRemainingSeconds > 0L
    AlertDialog(
        modifier = modifier,
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = value,
                    onValueChange = onValueChange,
                    label = { Text("Passphrase") },
                    singleLine = true,
                    enabled = !lockedOut && !isVerifying,
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    modifier = Modifier.fillMaxWidth(),
                )
                if (lockedOut) {
                    Text("Too many attempts. Try again in ${lockoutRemainingSeconds}s.")
                }
                error?.let { Text(it) }
            }
        },
        confirmButton = {
            TextButton(
                onClick = onConfirm,
                enabled = value.isNotEmpty() && !lockedOut && !isVerifying,
            ) {
                Text(confirmLabel)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss, enabled = !isVerifying) { Text("Cancel") }
        },
    )
}
