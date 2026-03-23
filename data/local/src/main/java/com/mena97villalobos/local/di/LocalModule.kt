package com.mena97villalobos.local.di

import androidx.room.Room
import com.mena97villalobos.domain.repository.WarrantyRepository
import com.mena97villalobos.local.dao.WarrantyDao
import com.mena97villalobos.local.database.LifeCompanionDatabase
import com.mena97villalobos.local.repository.WarrantyRepositoryImpl
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val localModule = module {
    single<LifeCompanionDatabase> {
        Room.databaseBuilder(
            androidContext(),
            LifeCompanionDatabase::class.java,
            "app_database",
        ).build()
    }
    single<WarrantyDao> { get<LifeCompanionDatabase>().warrantyDao() }
    single<WarrantyRepository> { WarrantyRepositoryImpl(get(), get()) }
}
