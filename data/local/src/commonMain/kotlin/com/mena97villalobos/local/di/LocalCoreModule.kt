package com.mena97villalobos.local.di

import com.mena97villalobos.domain.repository.AppLockRepository
import com.mena97villalobos.domain.repository.ProfileRepository
import com.mena97villalobos.domain.repository.WarrantyRepository
import com.mena97villalobos.domain.security.AppLockAutoLocker
import com.mena97villalobos.domain.security.AppLockManager
import com.mena97villalobos.local.dao.UserProfileDao
import com.mena97villalobos.local.dao.WarrantyDao
import com.mena97villalobos.local.database.LifeCompanionDatabase
import com.mena97villalobos.local.repository.AppLockRepositoryImpl
import com.mena97villalobos.local.repository.ProfileRepositoryImpl
import com.mena97villalobos.local.repository.WarrantyRepositoryImpl
import org.koin.dsl.module

internal val localCoreModule = module {
    includes(warrantyUseCaseModule, profileUseCaseModule)
    single<WarrantyDao> { get<LifeCompanionDatabase>().warrantyDao() }
    single<WarrantyRepository> { WarrantyRepositoryImpl(get(), get()) }

    single<UserProfileDao> { get<LifeCompanionDatabase>().userProfileDao() }
    single<ProfileRepository> { ProfileRepositoryImpl(get()) }
    single<AppLockRepository> { AppLockRepositoryImpl(get(), get()) }
    single { AppLockManager(get()) }
    // lifecycleObserver (AppLifecycleObserver) is bound in the host's platformSecurityModule.
    single { AppLockAutoLocker(get(), get(), get()) }
}
