package com.mena97villalobos.remote.repository

import com.mena97villalobos.domain.model.ExchangeRate
import com.mena97villalobos.domain.model.Indicator
import com.mena97villalobos.domain.repository.ExchangeRateRepository
import com.mena97villalobos.remote.client.service.ExchangeRateApi
import com.mena97villalobos.remote.model.toDomainModel

class ExchangeRateRepositoryImpl(private val api: ExchangeRateApi) : ExchangeRateRepository {
    override suspend fun getExchangeRate(indicator: Indicator): ExchangeRate = when (indicator) {
        Indicator.USD_TO_CRC -> api.getBuyRate().toDomainModel()
        Indicator.CRC_TO_USD -> api.getSellRate().toDomainModel()
    }
}
