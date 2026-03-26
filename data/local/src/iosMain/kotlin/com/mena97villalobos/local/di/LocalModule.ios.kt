package com.mena97villalobos.local.di

import com.mena97villalobos.local.database.LifeCompanionDatabase
import com.mena97villalobos.local.database.getLifeCompanionDatabaseBuilder
import com.mena97villalobos.local.database.getRoomDatabase
import org.koin.dsl.module

/**
 * Room, [WarrantyRepository], and warranty use cases ([warrantyUseCaseModule]).
 *
 * Combine with `DispatcherService` and `com.mena97villalobos.remote.di.iosRemoteModule` for a full data layer graph.
 */
val iosLocalModule = module {
    includes(localCoreModule)
    single<LifeCompanionDatabase> {
        getRoomDatabase(getLifeCompanionDatabaseBuilder())
    }
}
