package com.mena97villalobos.lifecompanion.di

import com.mena97villalobos.lifecompanion.ui.dashboard.DashboardViewModel
import com.mena97villalobos.lifecompanion.ui.lock.LockViewModel
import com.mena97villalobos.lifecompanion.ui.onboarding.OnboardingViewModel
import com.mena97villalobos.lifecompanion.ui.root.RootViewModel
import com.mena97villalobos.lifecompanion.ui.settings.SettingsViewModel
import com.mena97villalobos.lifecompanion.ui.settings.passphrase.PassphraseSettingsViewModel
import com.mena97villalobos.lifecompanion.ui.warranty.add.AddEditWarrantyViewModel
import com.mena97villalobos.lifecompanion.ui.warranty.list.WarrantyListViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel { DashboardViewModel(get(), get()) }
    viewModel {
        WarrantyListViewModel(
            getWarranties = get(),
            deleteWarranty = get(),
        )
    }

    viewModel {
        AddEditWarrantyViewModel(
            addWarranty = get(),
            updateWarranty = get(),
            uploadImage = get(),
        )
    }

    // App lock + onboarding (issue #7)
    viewModel { RootViewModel(observeProfile = get(), appLockManager = get(), autoLocker = get()) }
    viewModel {
        OnboardingViewModel(
            saveProfile = get(),
            setupPin = get(),
            setBiometricEnabled = get(),
            appLockManager = get(),
            biometricAuthenticator = get(),
        )
    }
    viewModel {
        LockViewModel(
            appLockManager = get(),
            observeBiometricEnabled = get(),
            biometricAuthenticator = get(),
        )
    }
    viewModel {
        SettingsViewModel(
            observeBiometricEnabled = get(),
            setBiometricEnabled = get(),
            setupPin = get(),
            getInactivityTimeout = get(),
            setInactivityTimeout = get(),
            biometricAuthenticator = get(),
        )
    }
    viewModel {
        PassphraseSettingsViewModel(
            observePassphraseEnabled = get(),
            enablePassphrase = get(),
            disablePassphrase = get(),
            changePassphrase = get(),
        )
    }
}
