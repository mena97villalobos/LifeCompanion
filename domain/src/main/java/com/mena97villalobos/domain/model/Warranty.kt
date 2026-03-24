package com.mena97villalobos.domain.model

import java.time.LocalDate
import java.time.temporal.ChronoUnit

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
        get() = LocalDate.now().isAfter(expiryDate)

    val daysUntilExpiry: Int
        get() = ChronoUnit.DAYS.between(LocalDate.now(), expiryDate).toInt()
}
