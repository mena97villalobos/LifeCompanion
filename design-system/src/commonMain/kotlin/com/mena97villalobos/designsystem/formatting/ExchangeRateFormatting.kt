package com.mena97villalobos.designsystem.formatting

import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import kotlin.math.absoluteValue
import kotlin.math.pow
import kotlin.math.roundToLong
import kotlin.time.Clock

/** Locale-neutral CRC / percentage / date helpers for exchange UI (shared on Android and iOS). */
object ExchangeRateFormatting {
    fun formatCurrencyDifference(value: Double): String {
        val formatted = roundToDecimals(value.absoluteValue, fractionDigits = 2)
        return "₡$formatted"
    }

    fun formatPercentage(value: Double, maxFractionDigits: Int): String =
        roundToDecimals(value, maxFractionDigits)

    fun formatShortDateToday(): String {
        val d = Clock.System.todayIn(TimeZone.currentSystemDefault())
        val monthShort =
            d.month.name.take(3).lowercase().replaceFirstChar { it.uppercaseChar() }
        val day = d.day.toString().padStart(2, '0')
        return "$day $monthShort"
    }

    private fun roundToDecimals(value: Double, fractionDigits: Int): String {
        val factor = 10.0.pow(fractionDigits)
        val rounded = (value * factor).roundToLong().toDouble() / factor
        val s = rounded.toString()
        val dot = s.indexOf('.')
        return if (dot == -1) {
            "$s." + "0".repeat(fractionDigits)
        } else {
            val intPart = s.substring(0, dot)
            val frac = s.substring(dot + 1).padEnd(fractionDigits, '0').take(fractionDigits)
            "$intPart.$frac"
        }
    }
}
