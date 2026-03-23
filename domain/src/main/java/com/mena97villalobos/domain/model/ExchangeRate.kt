package com.mena97villalobos.domain.model

import androidx.annotation.StringRes
import com.mena97villalobos.domain.R

data class ExchangeRate(
    val indicator: Indicator,
    val date: String,
    val rate: Double,
    val previousRate: Double,
)

enum class Indicator(@param:StringRes val textResource: Int) {
    USD_TO_CRC(R.string.usd_to_crc),
    CRC_TO_USD(R.string.crc_to_usd),
}
