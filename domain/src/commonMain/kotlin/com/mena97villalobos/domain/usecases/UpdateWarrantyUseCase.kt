package com.mena97villalobos.domain.usecases

import com.mena97villalobos.domain.model.Warranty
import com.mena97villalobos.domain.repository.WarrantyRepository

/** Updates an existing warranty row. */
class UpdateWarrantyUseCase(
    private val repository: WarrantyRepository,
) {
    suspend operator fun invoke(warranty: Warranty) {
        repository.updateWarranty(warranty)
    }
}
