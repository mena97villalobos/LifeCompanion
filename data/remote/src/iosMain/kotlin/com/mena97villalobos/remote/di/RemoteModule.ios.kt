package com.mena97villalobos.remote.di

import com.mena97villalobos.domain.services.MinioService
import com.mena97villalobos.remote.services.IosImageUriReader
import com.mena97villalobos.remote.services.ImageUriReader
import com.mena97villalobos.remote.services.MinioS3KtorService
import org.koin.dsl.module

val iosRemoteModule = module {
    includes(remoteCoreModule)
    single<ImageUriReader> { IosImageUriReader() }
    single<MinioService> {
        MinioS3KtorService(
            engine = get(),
            dispatcherService = get(),
            imageUriReader = get(),
        )
    }
}
