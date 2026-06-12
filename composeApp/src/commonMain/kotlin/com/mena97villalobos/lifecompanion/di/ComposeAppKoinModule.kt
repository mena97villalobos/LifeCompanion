package com.mena97villalobos.lifecompanion.di

import com.mena97villalobos.lifecompanion.security.platformSecurityModule
import org.koin.dsl.module

/** Shared Koin graph for Android and iOS hosts (ViewModels + dispatchers + platform security). */
val composeAppKoinModule = module {
    includes(
        coroutinesModules,
        viewModelModule,
        platformSecurityModule,
    )
}
