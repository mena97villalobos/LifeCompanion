package com.mena97villalobos.remote.client.service

import com.mena97villalobos.remote.model.ExchangeRateResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get

private const val EXCHANGE_RATE_SELL_ENDPOINT = "exchange-rate/sell"
private const val EXCHANGE_RATE_BUY_ENDPOINT = "exchange-rate/buy"

/** Low-level exchange-rate API client for buy/sell endpoints. */
class ExchangeRateApi(
    private val client: HttpClient,
) {
    /** Returns provider sell-rate payload (used for USD -> CRC in domain semantics). */
    suspend fun getSellRate(): ExchangeRateResponse = client.get(EXCHANGE_RATE_SELL_ENDPOINT).body()

    /** Returns provider buy-rate payload (used for CRC -> USD in domain semantics). */
    suspend fun getBuyRate(): ExchangeRateResponse = client.get(EXCHANGE_RATE_BUY_ENDPOINT).body()
}
