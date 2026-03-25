package com.mena97villalobos.local.di

import com.mena97villalobos.domain.repository.WarrantyRepository
import com.mena97villalobos.local.dao.WarrantyDao
import com.mena97villalobos.local.database.LifeCompanionDatabase
import com.mena97villalobos.local.database.getLifeCompanionDatabaseBuilder
import com.mena97villalobos.local.database.getRoomDatabase
import com.mena97villalobos.local.repository.WarrantyRepositoryImpl
import org.koin.dsl.module

/**
 * Room, [WarrantyRepository], and warranty use cases ([warrantyUseCaseModule]).
 *
 * Combine with `DispatcherService` and `com.mena97villalobos.remote.di.iosRemoteModule` for a full data layer graph.
 */
val iosLocalModule = module {
    includes(warrantyUseCaseModule)
    single<LifeCompanionDatabase> {
        getRoomDatabase(getLifeCompanionDatabaseBuilder())
    }
    single<WarrantyDao> { get<LifeCompanionDatabase>().warrantyDao() }
    single<WarrantyRepository> { WarrantyRepositoryImpl(get(), get()) }
}
