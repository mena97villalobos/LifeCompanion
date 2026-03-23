package com.mena97villalobos.remote.model

import com.mena97villalobos.domain.model.ExchangeRate
import com.mena97villalobos.domain.model.Indicator
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ExchangeRateResponse(
    val indicator: String,
    val date: String,
    val rate: Double,
    @SerialName("rate_previous")
    val ratePrevious: Double,
)

fun ExchangeRateResponse.toDomainModel(): ExchangeRate {
    val indicatorEnum = when (indicator) {
        "USD to CRC" -> Indicator.USD_TO_CRC
        "CRC to USD" -> Indicator.CRC_TO_USD
        else -> throw IllegalArgumentException("Unknown indicator: $indicator")
    }
    return ExchangeRate(
        indicator = indicatorEnum,
        date = date,
        rate = rate,
        previousRate = ratePrevious,
    )
}
