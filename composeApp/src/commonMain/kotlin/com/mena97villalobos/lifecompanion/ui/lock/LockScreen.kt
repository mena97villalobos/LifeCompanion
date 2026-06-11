package com.mena97villalobos.lifecompanion.ui.lock

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.compose.viewmodel.koinViewModel

/** Lock screen shown whenever the app is locked: PIN entry plus optional biometric unlock. */
@Composable
fun LockScreen(
    modifier: Modifier = Modifier,
    viewModel: LockViewModel = koinViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    // Offer the biometric prompt automatically when the screen appears and biometrics are enabled.
    LaunchedEffect(state.canOfferBiometric) {
        if (state.canOfferBiometric) viewModel.authenticateWithBiometric()
    }

    // While locked out, keep the remaining-time countdown fresh.
    LaunchedEffect(state.isLockedOut) {
        while (state.isLockedOut) {
            viewModel.refreshLockout()
            kotlinx.coroutines.delay(1_000L)
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text("LifeCompanion is locked")

        OutlinedTextField(
            value = state.pin,
            onValueChange = viewModel::onPinChange,
            label = { Text("Enter PIN") },
            singleLine = true,
            enabled = !state.isLockedOut && !state.isVerifying,
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
            modifier = Modifier.fillMaxWidth(),
        )

        if (state.isLockedOut) {
            val seconds = (state.lockoutRemainingMillis + 999L) / 1_000L
            Text("Too many attempts. Try again in ${seconds}s.")
        }

        state.error?.let { Text(it) }

        Button(
            onClick = viewModel::submitPin,
            enabled = state.pin.isNotEmpty() && !state.isLockedOut && !state.isVerifying,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text("Unlock")
        }

        if (state.biometricEnabled && state.biometricAvailable) {
            OutlinedButton(
                onClick = viewModel::authenticateWithBiometric,
                enabled = !state.isLockedOut,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text("Use biometrics")
            }
        }
    }
}
