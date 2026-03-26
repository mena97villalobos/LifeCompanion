package com.mena97villalobos.remote.di

import com.mena97villalobos.domain.repository.ExchangeRateRepository
import com.mena97villalobos.domain.usecases.GetBuyRateUseCase
import com.mena97villalobos.domain.usecases.GetSellRateUseCase
import com.mena97villalobos.remote.client.createHttpClientEngine
import com.mena97villalobos.remote.client.provideExchangeHttpClient
import com.mena97villalobos.remote.client.service.ExchangeRateApi
import com.mena97villalobos.remote.repository.ExchangeRateRepositoryImpl
import io.ktor.client.HttpClient
import io.ktor.client.engine.HttpClientEngine
import org.koin.dsl.module

/**
 * Exchange HTTP client, API, [com.mena97villalobos.domain.repository.ExchangeRateRepository], and
 * buy/sell rate use cases. Shared by Android `remoteModule` and iOS `iosRemoteModule`; it does not
 * register MinIO (platform-specific modules add [com.mena97villalobos.domain.services.MinioService]).
 */
val remoteCoreModule = module {
    single<HttpClientEngine> { createHttpClientEngine() }
    single<HttpClient> { provideExchangeHttpClient(get()) }
    single { ExchangeRateApi(get()) }
    single<ExchangeRateRepository> { ExchangeRateRepositoryImpl(get()) }
    factory { GetSellRateUseCase(get()) }
    factory { GetBuyRateUseCase(get()) }
}
