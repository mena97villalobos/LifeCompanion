package com.mena97villalobos.local.di

import com.mena97villalobos.domain.repository.WarrantyRepository
import com.mena97villalobos.local.dao.WarrantyDao
import com.mena97villalobos.local.database.LifeCompanionDatabase
import com.mena97villalobos.local.repository.WarrantyRepositoryImpl
import org.koin.dsl.module

internal val localCoreModule = module {
    includes(warrantyUseCaseModule)
    single<WarrantyDao> { get<LifeCompanionDatabase>().warrantyDao() }
    single<WarrantyRepository> { WarrantyRepositoryImpl(get(), get()) }
}
