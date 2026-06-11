package com.mena97villalobos.local.di

import com.mena97villalobos.domain.security.PinHasher
import com.mena97villalobos.domain.security.SecureStorage
import com.mena97villalobos.local.database.LifeCompanionDatabase
import com.mena97villalobos.local.database.getLifeCompanionDatabaseBuilder
import com.mena97villalobos.local.database.getRoomDatabase
import com.mena97villalobos.local.security.AndroidArgon2PinHasher
import com.mena97villalobos.local.security.AndroidSecureStorage
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

/**
 * Android Room database, [com.mena97villalobos.domain.repository.WarrantyRepository],
 * [warrantyUseCaseModule], and the app-lock platform services (encrypted secure storage + Argon2id
 * PIN hashing). Requires `androidContext()` from Koin.
 */
val localModule = module {
    includes(localCoreModule)
    single<LifeCompanionDatabase> {
        getRoomDatabase(getLifeCompanionDatabaseBuilder(androidContext()))
    }
    single<SecureStorage> { AndroidSecureStorage(androidContext()) }
    single<PinHasher> { AndroidArgon2PinHasher() }
}
