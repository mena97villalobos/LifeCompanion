package com.mena97villalobos.domain.repository

import com.mena97villalobos.domain.model.ExchangeRate
import com.mena97villalobos.domain.model.Indicator

fun interface ExchangeRateRepository {
    suspend fun getExchangeRate(indicator: Indicator): ExchangeRate
}
