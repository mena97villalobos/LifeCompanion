package com.mena97villalobos.lifecompanion.security

import com.mena97villalobos.domain.security.AppLifecycleObserver
import com.mena97villalobos.domain.security.BiometricAuthenticator
import com.mena97villalobos.domain.security.PinHasher
import org.koin.core.module.Module
import org.koin.dsl.module

actual val platformSecurityModule: Module = module {
    single<BiometricAuthenticator> { IosBiometricAuthenticator() }
    single<AppLifecycleObserver> { IosAppLifecycleObserver() }
    single<PinHasher> {
        val argon2 = requireNotNull(IosArgon2Holder.argon2) {
            "IosArgon2Holder.argon2 must be set before Koin starts (see LifeCompanionApp.swift)"
        }
        IosPinHasher(argon2)
    }
}
