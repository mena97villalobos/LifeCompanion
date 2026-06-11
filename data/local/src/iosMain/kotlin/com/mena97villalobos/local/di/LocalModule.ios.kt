package com.mena97villalobos.local.di

import com.mena97villalobos.domain.security.SecureStorage
import com.mena97villalobos.local.database.LifeCompanionDatabase
import com.mena97villalobos.local.database.getLifeCompanionDatabaseBuilder
import com.mena97villalobos.local.database.getRoomDatabase
import com.mena97villalobos.local.security.IosSecureStorage
import org.koin.dsl.module

/**
 * Room, repositories, warranty + profile use cases ([localCoreModule]), and the iOS Keychain-backed
 * [SecureStorage].
 *
 * The Argon2id [com.mena97villalobos.domain.security.PinHasher] is provided from the iOS app side
 * (Swift `Argon2Swift` package, bridged in `IosComposeEntry`), since it is delivered via SPM rather
 * than bundled into the KMP module. Combine with `DispatcherService` and
 * `com.mena97villalobos.remote.di.iosRemoteModule` for a full data-layer graph.
 */
val iosLocalModule = module {
    includes(localCoreModule)
    single<LifeCompanionDatabase> {
        getRoomDatabase(getLifeCompanionDatabaseBuilder())
    }
    single<SecureStorage> { IosSecureStorage() }
}
