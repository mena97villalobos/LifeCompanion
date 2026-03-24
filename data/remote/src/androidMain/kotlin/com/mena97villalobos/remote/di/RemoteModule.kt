package com.mena97villalobos.remote.di

import com.mena97villalobos.domain.repository.ExchangeRateRepository
import com.mena97villalobos.domain.services.MinioService
import com.mena97villalobos.domain.usecases.AddWarrantyUseCase
import com.mena97villalobos.domain.usecases.DeleteWarrantyUseCase
import com.mena97villalobos.domain.usecases.GetBuyRateUseCase
import com.mena97villalobos.domain.usecases.GetSellRateUseCase
import com.mena97villalobos.domain.usecases.GetWarrantiesUseCase
import com.mena97villalobos.domain.usecases.UpdateWarrantyUseCase
import com.mena97villalobos.domain.usecases.UploadWarrantyImageUseCase
import com.mena97villalobos.remote.BuildKonfig
import com.mena97villalobos.remote.client.provideHttpClient
import com.mena97villalobos.remote.client.service.ExchangeRateApi
import com.mena97villalobos.remote.repository.ExchangeRateRepositoryImpl
import com.mena97villalobos.remote.services.MinioServiceImpl
import io.minio.MinioClient
import org.koin.dsl.module

internal val minioModule = module {
    single<MinioClient> {
        MinioClient.builder()
            .endpoint(BuildKonfig.MINIO_ENDPOINT)
            .credentials(
                BuildKonfig.MINIO_ENDPOINT_ACCESS_KEY,
                BuildKonfig.MINIO_ENDPOINT_SECRET_KEY,
            )
            .build()
    }

    single<MinioService> {
        MinioServiceImpl(
            dispatcherService = get(),
            context = get(),
            minioClient = get(),
            bucketName = BuildKonfig.MINIO_BUCKET_NAME,
        )
    }
}

val remoteModule = module {
    includes(minioModule)
    single { provideHttpClient() }
    single { ExchangeRateApi(get()) }
    single<ExchangeRateRepository> { ExchangeRateRepositoryImpl(get()) }
    factory { GetSellRateUseCase(get()) }
    factory { GetBuyRateUseCase(get()) }
    factory { GetWarrantiesUseCase(get()) }
    factory { AddWarrantyUseCase(get()) }
    factory { UpdateWarrantyUseCase(get()) }
    factory { DeleteWarrantyUseCase(get()) }
    factory { UploadWarrantyImageUseCase(get()) }
}
