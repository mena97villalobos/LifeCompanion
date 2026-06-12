package com.mena97villalobos.local.mappers

import com.mena97villalobos.domain.model.AppLocale
import com.mena97villalobos.domain.model.Currency
import com.mena97villalobos.domain.model.UserProfile
import com.mena97villalobos.local.entities.UserProfileEntity

fun UserProfileEntity.toDomain() = UserProfile(
    id = id,
    displayName = displayName,
    currency = Currency.fromCode(currencyCode),
    locale = AppLocale.fromTag(localeTag),
)

fun UserProfile.toEntity() = UserProfileEntity(
    id = id,
    displayName = displayName,
    currencyCode = currency.code,
    localeTag = locale.tag,
)
