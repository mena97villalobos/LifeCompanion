package com.mena97villalobos.lifecompanion.di

import com.mena97villalobos.domain.services.DispatcherService
import com.mena97villalobos.domain.services.createDefaultDispatcherService
import org.koin.dsl.module

val coroutinesModules = module {
    single<DispatcherService> { createDefaultDispatcherService() }
}
