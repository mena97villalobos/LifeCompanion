package com.mena97villalobos.lifecompanion.ui.onboarding

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
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
import com.mena97villalobos.domain.model.AppLocale
import com.mena97villalobos.domain.model.Currency
import org.koin.compose.viewmodel.koinViewModel

/** First-launch onboarding wizard: welcome -> profile -> app-lock setup. */
@Composable
fun OnboardingScreen(
    modifier: Modifier = Modifier,
    viewModel: OnboardingViewModel = koinViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        when (state.step) {
            OnboardingStep.WELCOME -> WelcomeStep(onContinue = viewModel::next)
            OnboardingStep.PROFILE -> ProfileStep(state = state, viewModel = viewModel)
            OnboardingStep.APP_LOCK -> AppLockStep(state = state, viewModel = viewModel)
        }

        state.error?.let { Text(it) }
    }
}

@Composable
private fun WelcomeStep(onContinue: () -> Unit) {
    Text("Welcome to LifeCompanion")
    Text(
        "Track your warranties, exchange rates and finances — all stored privately on this device. " +
            "No account needed.",
    )
    Spacer(Modifier.size(8.dp))
    Button(onClick = onContinue, modifier = Modifier.fillMaxWidth()) { Text("Get started") }
}

@Composable
private fun ProfileStep(
    state: OnboardingUiState,
    viewModel: OnboardingViewModel,
) {
    Text("Set up your profile")
    OutlinedTextField(
        value = state.displayName,
        onValueChange = viewModel::onDisplayNameChange,
        label = { Text("Display name") },
        singleLine = true,
        modifier = Modifier.fillMaxWidth(),
    )

    Text("Currency")
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        Currency.entries.forEach { currency ->
            FilterChip(
                selected = state.currency == currency,
                onClick = { viewModel.onCurrencySelected(currency) },
                label = { Text("${currency.code} ${currency.symbol}") },
            )
        }
    }

    Text("Language")
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        AppLocale.entries.forEach { locale ->
            FilterChip(
                selected = state.locale == locale,
                onClick = { viewModel.onLocaleSelected(locale) },
                label = { Text(locale.tag) },
            )
        }
    }

    StepNavigation(
        onBack = viewModel::back,
        onNext = viewModel::next,
        nextEnabled = state.canContinueProfile,
        nextLabel = "Continue",
    )
}

@Composable
private fun AppLockStep(
    state: OnboardingUiState,
    viewModel: OnboardingViewModel,
) {
    Text("Secure your app")
    Text("Set a 6-digit PIN to lock LifeCompanion.")

    PinField(value = state.pin, onValueChange = viewModel::onPinChange, label = "PIN")
    PinField(value = state.confirmPin, onValueChange = viewModel::onConfirmPinChange, label = "Confirm PIN")

    if (state.biometricAvailable) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text("Enable biometric unlock")
            Switch(checked = state.enableBiometric, onCheckedChange = viewModel::onBiometricToggle)
        }
    }

    if (state.isSaving) {
        CircularProgressIndicator()
    } else {
        StepNavigation(
            onBack = viewModel::back,
            onNext = viewModel::finish,
            nextEnabled = state.canFinish,
            nextLabel = "Finish",
        )
    }
}

@Composable
private fun PinField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        singleLine = true,
        visualTransformation = PasswordVisualTransformation(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
        modifier = Modifier.fillMaxWidth(),
    )
}

@Composable
private fun StepNavigation(
    onBack: () -> Unit,
    onNext: () -> Unit,
    nextEnabled: Boolean,
    nextLabel: String,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        TextButton(onClick = onBack) { Text("Back") }
        Button(onClick = onNext, enabled = nextEnabled) { Text(nextLabel) }
    }
}
