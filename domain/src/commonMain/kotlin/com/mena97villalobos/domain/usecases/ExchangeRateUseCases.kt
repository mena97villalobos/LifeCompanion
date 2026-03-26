package com.mena97villalobos.domain.usecases

import com.mena97villalobos.domain.model.Indicator
import com.mena97villalobos.domain.repository.ExchangeRateRepository

/** Resolves the USD→CRC sell-side rate. */
class GetSellRateUseCase(private val repository: ExchangeRateRepository) {
    suspend operator fun invoke() = repository.getExchangeRate(Indicator.USD_TO_CRC)
}

/** Resolves the CRC→USD buy-side rate. */
class GetBuyRateUseCase(private val repository: ExchangeRateRepository) {
    suspend operator fun invoke() = repository.getExchangeRate(Indicator.CRC_TO_USD)
}
