package com.mena97villalobos.local.repository

import com.mena97villalobos.domain.model.Warranty
import com.mena97villalobos.domain.repository.WarrantyRepository
import com.mena97villalobos.domain.services.MinioService
import com.mena97villalobos.local.dao.WarrantyDao
import com.mena97villalobos.local.mappers.toDomain
import com.mena97villalobos.local.mappers.toEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class WarrantyRepositoryImpl(
    private val dao: WarrantyDao,
    private val minioService: MinioService,
) : WarrantyRepository {

    override fun getAllWarranties(): Flow<List<Warranty>> = dao.getAll().map { list ->
        list.map { it.toDomain() }
    }

    override suspend fun insertWarranty(warranty: Warranty) = dao.insert(warranty.toEntity())

    override suspend fun updateWarranty(warranty: Warranty) = dao.update(warranty.toEntity())

    override suspend fun deleteWarranty(id: Long) = dao.delete(id)

    override suspend fun uploadImage(imageUri: String): String = minioService.upload(imageUri)
}
