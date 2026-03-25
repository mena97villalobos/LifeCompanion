package com.mena97villalobos.remote.client.service

import com.mena97villalobos.remote.model.ExchangeRateResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get

private const val EXCHANGE_RATE_SELL_ENDPOINT = "exchange-rate/sell"
private const val EXCHANGE_RATE_BUY_ENDPOINT = "exchange-rate/buy"

class ExchangeRateApi(
    private val client: HttpClient,
) {
    suspend fun getSellRate(): ExchangeRateResponse = client.get(EXCHANGE_RATE_SELL_ENDPOINT).body()

    suspend fun getBuyRate(): ExchangeRateResponse = client.get(EXCHANGE_RATE_BUY_ENDPOINT).body()
}
