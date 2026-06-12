package com.mena97villalobos.domain.repository

import com.mena97villalobos.domain.model.UserProfile
import kotlinx.coroutines.flow.Flow

/**
 * Persistence for the single device-local [UserProfile].
 *
 * The profile is created once during onboarding and read on every launch to decide whether to show
 * the onboarding wizard (no profile) or the dashboard/lock screen (profile present).
 */
interface ProfileRepository {
    /** Cold [Flow] of the current profile, emitting null until onboarding creates one. */
    fun observeProfile(): Flow<UserProfile?>

    /** Returns the current profile or null when onboarding has not completed. */
    suspend fun getProfile(): UserProfile?

    /** Inserts or replaces the single local profile. */
    suspend fun saveProfile(profile: UserProfile)

    /** Removes the profile (e.g. on a full reset). */
    suspend fun clearProfile()
}
