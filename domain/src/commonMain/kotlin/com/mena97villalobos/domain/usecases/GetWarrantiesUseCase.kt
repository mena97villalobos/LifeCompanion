package com.mena97villalobos.domain.usecases

import com.mena97villalobos.domain.model.Warranty
import com.mena97villalobos.domain.repository.WarrantyRepository
import kotlinx.coroutines.flow.Flow

/** Exposes the warranty list stream from [WarrantyRepository]. */
class GetWarrantiesUseCase(
    private val repository: WarrantyRepository,
) {
    operator fun invoke(): Flow<List<Warranty>> = repository.getAllWarranties()
}
