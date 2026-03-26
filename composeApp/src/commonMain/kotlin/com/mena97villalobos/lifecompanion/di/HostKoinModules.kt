package com.mena97villalobos.lifecompanion.di

import org.koin.core.module.Module

/** Shared host-level Koin module assembly for Android and iOS app bootstrap. */
fun hostKoinModules(
    localModule: Module,
    remoteModule: Module,
): List<Module> = listOf(
    composeAppKoinModule,
    localModule,
    remoteModule,
)
