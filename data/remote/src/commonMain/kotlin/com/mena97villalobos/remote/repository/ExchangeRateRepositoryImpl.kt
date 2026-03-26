package com.mena97villalobos.remote.repository

import com.mena97villalobos.domain.model.ExchangeRate
import com.mena97villalobos.domain.model.Indicator
import com.mena97villalobos.domain.repository.ExchangeRateRepository
import com.mena97villalobos.remote.client.service.ExchangeRateApi
import com.mena97villalobos.remote.model.toDomainModel

internal enum class ExchangeEndpoint {
    SELL,
    BUY,
}

internal fun endpointFor(indicator: Indicator): ExchangeEndpoint = when (indicator) {
    Indicator.USD_TO_CRC -> ExchangeEndpoint.SELL
    Indicator.CRC_TO_USD -> ExchangeEndpoint.BUY
}

class ExchangeRateRepositoryImpl(private val api: ExchangeRateApi) : ExchangeRateRepository {
    override suspend fun getExchangeRate(indicator: Indicator): ExchangeRate =
        when (endpointFor(indicator)) {
            ExchangeEndpoint.SELL -> api.getSellRate().toDomainModel()
            ExchangeEndpoint.BUY -> api.getBuyRate().toDomainModel()
    }
}
