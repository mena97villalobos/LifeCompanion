package com.mena97villalobos.lifecompanion.ui.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.compose.viewmodel.koinViewModel

/** App-lock settings: toggle biometric unlock and change the PIN (issue #7). */
@Composable
fun SettingsScreen(
    modifier: Modifier = Modifier,
    viewModel: SettingsViewModel = koinViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text("Security")

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column {
                Text("Biometric unlock")
                if (!state.biometricAvailable) Text("Not available on this device")
            }
            Switch(
                checked = state.biometricEnabled,
                onCheckedChange = viewModel::onBiometricToggle,
                enabled = state.biometricAvailable,
            )
        }

        HorizontalDivider()

        Column {
            val minutes = state.inactivityTimeoutMinutes
            Text("Auto-lock after inactivity: $minutes min")
            Slider(
                value = minutes.toFloat(),
                onValueChange = { viewModel.onInactivityTimeoutChange(it.toInt()) },
                onValueChangeFinished = { viewModel.onInactivityTimeoutCommit(minutes) },
                valueRange = state.inactivityTimeoutRange.first.toFloat()..state.inactivityTimeoutRange.last.toFloat(),
                steps = (state.inactivityTimeoutRange.last - state.inactivityTimeoutRange.first - 1).coerceAtLeast(0),
                modifier = Modifier.fillMaxWidth(),
            )
        }

        HorizontalDivider()

        Text("Change PIN")
        OutlinedTextField(
            value = state.changePinValue,
            onValueChange = viewModel::onChangePinValue,
            label = { Text("New PIN") },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
            modifier = Modifier.fillMaxWidth(),
        )
        OutlinedTextField(
            value = state.confirmPinValue,
            onValueChange = viewModel::onConfirmPinValue,
            label = { Text("Confirm new PIN") },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
            modifier = Modifier.fillMaxWidth(),
        )
        Button(
            onClick = viewModel::savePin,
            enabled = state.canSavePin,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text("Update PIN")
        }

        state.message?.let { Text(it) }
    }
}
