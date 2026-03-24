package com.mena97villalobos.local.di

import com.mena97villalobos.domain.repository.WarrantyRepository
import com.mena97villalobos.local.dao.WarrantyDao
import com.mena97villalobos.local.database.LifeCompanionDatabase
import com.mena97villalobos.local.database.getLifeCompanionDatabaseBuilder
import com.mena97villalobos.local.database.getRoomDatabase
import com.mena97villalobos.local.repository.WarrantyRepositoryImpl
import org.koin.dsl.module

val iosLocalModule = module {
    single<LifeCompanionDatabase> {
        getRoomDatabase(getLifeCompanionDatabaseBuilder())
    }
    single<WarrantyDao> { get<LifeCompanionDatabase>().warrantyDao() }
    single<WarrantyRepository> { WarrantyRepositoryImpl(get(), get()) }
}
