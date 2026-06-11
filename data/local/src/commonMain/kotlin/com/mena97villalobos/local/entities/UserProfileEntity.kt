package com.mena97villalobos.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.mena97villalobos.domain.model.UserProfile

/**
 * Persisted device-local profile. A single row (id == [UserProfile.SINGLETON_ID]) is expected.
 * Currency and locale are stored as their string code/tag so the schema is stable across enum edits.
 */
@Entity(tableName = "user_profile")
data class UserProfileEntity(
    @PrimaryKey
    val id: Long = UserProfile.SINGLETON_ID,
    val displayName: String,
    val currencyCode: String,
    val localeTag: String,
)
