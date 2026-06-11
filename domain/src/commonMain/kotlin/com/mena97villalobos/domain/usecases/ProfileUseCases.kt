package com.mena97villalobos.domain.usecases

import com.mena97villalobos.domain.model.UserProfile
import com.mena97villalobos.domain.repository.ProfileRepository
import kotlinx.coroutines.flow.Flow

/** Reads the current local profile, or null when onboarding has not completed. */
class GetProfileUseCase(
    private val repository: ProfileRepository,
) {
    suspend operator fun invoke(): UserProfile? = repository.getProfile()
}

/** Observes the local profile; emits null until onboarding creates one. */
class ObserveProfileUseCase(
    private val repository: ProfileRepository,
) {
    operator fun invoke(): Flow<UserProfile?> = repository.observeProfile()
}

/** Creates or updates the single device-local profile. */
class SaveProfileUseCase(
    private val repository: ProfileRepository,
) {
    suspend operator fun invoke(profile: UserProfile) = repository.saveProfile(profile)
}
