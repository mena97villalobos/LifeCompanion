package com.mena97villalobos.domain.usecases

import com.mena97villalobos.domain.repository.WarrantyRepository

/** Deletes a warranty by id. */
class DeleteWarrantyUseCase(
    private val repository: WarrantyRepository,
) {
    suspend operator fun invoke(id: Long) {
        repository.deleteWarranty(id)
    }
}
