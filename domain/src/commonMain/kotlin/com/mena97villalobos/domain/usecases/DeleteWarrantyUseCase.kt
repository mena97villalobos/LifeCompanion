package com.mena97villalobos.domain.usecases

import com.mena97villalobos.domain.repository.WarrantyRepository

class DeleteWarrantyUseCase(
    private val repository: WarrantyRepository,
) {
    suspend operator fun invoke(id: Long) {
        repository.deleteWarranty(id)
    }
}
