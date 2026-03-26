package com.mena97villalobos.domain.repository

import com.mena97villalobos.domain.model.ExchangeRate
import com.mena97villalobos.domain.model.Indicator

/**
 * Fetches current and previous exchange rates for a given [Indicator] (buy vs sell semantics are
 * expressed by which [Indicator] the caller passes).
 *
 * Implementations are expected to map:
 * - [Indicator.USD_TO_CRC] to sell-rate endpoint semantics
 * - [Indicator.CRC_TO_USD] to buy-rate endpoint semantics
 */
fun interface ExchangeRateRepository {
    /** Resolves an exchange rate snapshot; may throw for transport or parsing failures. */
    suspend fun getExchangeRate(indicator: Indicator): ExchangeRate
}
