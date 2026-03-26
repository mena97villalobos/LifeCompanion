package com.mena97villalobos.lifecompanion.di

import com.mena97villalobos.local.di.localModule
import com.mena97villalobos.remote.di.remoteModule
import org.koin.dsl.module

val appModule = module {
    includes(hostKoinModules(localModule, remoteModule))
}
