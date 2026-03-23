package com.mena97villalobos.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity(tableName = "warranties")
data class WarrantyEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val description: String,
    val storeName: String,
    val purchaseDate: LocalDate,
    val expiryDate: LocalDate,
    val notes: String?,
    val imageObjectId: String?,
)
