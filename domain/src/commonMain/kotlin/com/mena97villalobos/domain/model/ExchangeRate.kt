package com.mena97villalobos.domain.model

/** Snapshot of an exchange rate pair for dashboard display. */
data class ExchangeRate(
    val indicator: Indicator,
    val date: String,
    val rate: Double,
    val previousRate: Double,
)

/** Which FX pair the remote API should return ([displayName] is user-facing). */
enum class Indicator(val displayName: String) {
    USD_TO_CRC("USD to CRC"),
    CRC_TO_USD("CRC to USD"),
}
