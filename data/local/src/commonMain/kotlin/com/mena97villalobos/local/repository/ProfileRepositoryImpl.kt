package com.mena97villalobos.local.repository

import com.mena97villalobos.domain.model.UserProfile
import com.mena97villalobos.domain.repository.ProfileRepository
import com.mena97villalobos.local.dao.UserProfileDao
import com.mena97villalobos.local.mappers.toDomain
import com.mena97villalobos.local.mappers.toEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ProfileRepositoryImpl(
    private val dao: UserProfileDao,
) : ProfileRepository {

    override fun observeProfile(): Flow<UserProfile?> = dao.observe().map { it?.toDomain() }

    override suspend fun getProfile(): UserProfile? = dao.get()?.toDomain()

    override suspend fun saveProfile(profile: UserProfile) = dao.upsert(profile.toEntity())

    override suspend fun clearProfile() = dao.clear()
}
