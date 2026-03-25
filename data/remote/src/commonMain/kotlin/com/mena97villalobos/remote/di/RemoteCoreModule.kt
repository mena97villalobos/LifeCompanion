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

/** Exchange HTTP client, API, repository, and rate use cases only. */
val remoteCoreModule = module {
    single<HttpClientEngine> { createHttpClientEngine() }
    single<HttpClient> { provideExchangeHttpClient(get()) }
    single { ExchangeRateApi(get()) }
    single<ExchangeRateRepository> { ExchangeRateRepositoryImpl(get()) }
    factory { GetSellRateUseCase(get()) }
    factory { GetBuyRateUseCase(get()) }
}
