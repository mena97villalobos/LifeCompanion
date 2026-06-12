package com.mena97villalobos.local.di

import com.mena97villalobos.domain.usecases.ChangePassphraseUseCase
import com.mena97villalobos.domain.usecases.DisablePassphraseUseCase
import com.mena97villalobos.domain.usecases.EnablePassphraseUseCase
import com.mena97villalobos.domain.usecases.GetInactivityTimeoutUseCase
import com.mena97villalobos.domain.usecases.GetProfileUseCase
import com.mena97villalobos.domain.usecases.IsPassphraseEnabledUseCase
import com.mena97villalobos.domain.usecases.IsPinSetUseCase
import com.mena97villalobos.domain.usecases.SetInactivityTimeoutUseCase
import com.mena97villalobos.domain.usecases.ObserveBiometricEnabledUseCase
import com.mena97villalobos.domain.usecases.ObservePassphraseEnabledUseCase
import com.mena97villalobos.domain.usecases.ObserveProfileUseCase
import com.mena97villalobos.domain.usecases.SaveProfileUseCase
import com.mena97villalobos.domain.usecases.SetBiometricEnabledUseCase
import com.mena97villalobos.domain.usecases.SetupPinUseCase
import org.koin.dsl.module

/**
 * Profile and app-lock use cases (issue #7). They depend on
 * [com.mena97villalobos.domain.repository.ProfileRepository] /
 * [com.mena97villalobos.domain.repository.AppLockRepository], wired in [localCoreModule].
 */
val profileUseCaseModule = module {
    factory { GetProfileUseCase(get()) }
    factory { ObserveProfileUseCase(get()) }
    factory { SaveProfileUseCase(get()) }
    factory { SetupPinUseCase(get()) }
    factory { SetBiometricEnabledUseCase(get()) }
    factory { ObserveBiometricEnabledUseCase(get()) }
    factory { IsPinSetUseCase(get()) }
    factory { GetInactivityTimeoutUseCase(get()) }
    factory { SetInactivityTimeoutUseCase(get()) }

    // Sensitive-operations passphrase (issue #9).
    factory { ObservePassphraseEnabledUseCase(get()) }
    factory { IsPassphraseEnabledUseCase(get()) }
    factory { EnablePassphraseUseCase(get()) }
    factory { DisablePassphraseUseCase(get(), get()) }
    factory { ChangePassphraseUseCase(get(), get()) }
}
