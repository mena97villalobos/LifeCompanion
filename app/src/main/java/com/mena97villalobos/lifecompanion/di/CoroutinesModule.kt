package com.mena97villalobos.lifecompanion.di

import com.mena97villalobos.domain.services.DispatcherService
import kotlinx.coroutines.Dispatchers
import org.koin.dsl.module

private class DefaultDispatcherService : DispatcherService {
    override val io = Dispatchers.IO
    override val default = Dispatchers.Default
    override val main = Dispatchers.Main
}

val coroutinesModules = module {
    single<DispatcherService> { DefaultDispatcherService() }
}
