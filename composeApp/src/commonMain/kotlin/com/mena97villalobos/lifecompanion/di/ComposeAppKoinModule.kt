package com.mena97villalobos.lifecompanion.di

import org.koin.dsl.module

/** Shared Koin graph for Android and iOS hosts (ViewModels + dispatchers). */
val composeAppKoinModule = module {
    includes(
        coroutinesModules,
        viewModelModule,
    )
}
