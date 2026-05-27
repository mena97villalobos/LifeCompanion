package com.mena97villalobos.observability.di

import com.mena97villalobos.observability.CrashReporter
import com.mena97villalobos.observability.Observability
import org.koin.dsl.module

/**
 * Exposes the active [CrashReporter] for injection. Include alongside the other host modules; relies
 * on [Observability.init] having run at startup.
 */
val observabilityModule = module {
    single<CrashReporter> { Observability.crashReporter() }
}
