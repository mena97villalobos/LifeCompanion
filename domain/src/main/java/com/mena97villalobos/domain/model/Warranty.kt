package com.mena97villalobos.domain.model

import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.daysUntil
import kotlinx.datetime.todayIn
import kotlin.time.Clock

data class Warranty(
    val id: Long?,
    val description: String,
    val storeName: String,
    val purchaseDate: LocalDate,
    val expiryDate: LocalDate,
    val notes: String?,
    val imageObjectId: String?,
) {
    val isExpired: Boolean
        get() = today() > expiryDate

    val daysUntilExpiry: Int
        get() = today().daysUntil(expiryDate)
}

private fun today(): LocalDate = Clock.System.todayIn(TimeZone.currentSystemDefault())
