package com.mena97villalobos.local.mappers

import com.mena97villalobos.domain.model.Warranty
import com.mena97villalobos.local.entities.WarrantyEntity

fun WarrantyEntity.toDomain() = Warranty(
    id = id,
    description = description,
    storeName = storeName,
    purchaseDate = purchaseDate,
    expiryDate = expiryDate,
    notes = notes,
    imageObjectId = imageObjectId,
)

fun Warranty.toEntity() = WarrantyEntity(
    id = id ?: 0,
    description = description,
    storeName = storeName,
    purchaseDate = purchaseDate,
    expiryDate = expiryDate,
    notes = notes,
    imageObjectId = imageObjectId,
)
