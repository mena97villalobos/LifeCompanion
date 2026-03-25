package com.mena97villalobos.local.di

import com.mena97villalobos.domain.repository.WarrantyRepository
import com.mena97villalobos.local.dao.WarrantyDao
import com.mena97villalobos.local.database.LifeCompanionDatabase
import com.mena97villalobos.local.database.getLifeCompanionDatabaseBuilder
import com.mena97villalobos.local.database.getRoomDatabase
import com.mena97villalobos.local.repository.WarrantyRepositoryImpl
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val localModule = module {
    includes(warrantyUseCaseModule)
    single<LifeCompanionDatabase> {
        getRoomDatabase(getLifeCompanionDatabaseBuilder(androidContext()))
    }
    single<WarrantyDao> { get<LifeCompanionDatabase>().warrantyDao() }
    single<WarrantyRepository> { WarrantyRepositoryImpl(get(), get()) }
}
