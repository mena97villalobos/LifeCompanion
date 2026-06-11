package com.mena97villalobos.lifecompanion.security

import com.mena97villalobos.domain.security.BiometricAuthenticator
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.Module
import org.koin.dsl.module

actual val platformSecurityModule: Module = module {
    single<BiometricAuthenticator> { AndroidBiometricAuthenticator(androidContext()) }
}
