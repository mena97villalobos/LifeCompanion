package com.mena97villalobos.domain.model

data class ExchangeRate(
    val indicator: Indicator,
    val date: String,
    val rate: Double,
    val previousRate: Double,
)

enum class Indicator(val displayName: String) {
    USD_TO_CRC("USD to CRC"),
    CRC_TO_USD("CRC to USD"),
}
