package com.mena97villalobos.local.di

import com.mena97villalobos.local.database.LifeCompanionDatabase
import com.mena97villalobos.local.database.getLifeCompanionDatabaseBuilder
import com.mena97villalobos.local.database.getRoomDatabase
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

/**
 * Android Room database, [com.mena97villalobos.domain.repository.WarrantyRepository], and
 * [warrantyUseCaseModule]. Requires `androidContext()` from Koin.
 */
val localModule = module {
    includes(localCoreModule)
    single<LifeCompanionDatabase> {
        getRoomDatabase(getLifeCompanionDatabaseBuilder(androidContext()))
    }
}
