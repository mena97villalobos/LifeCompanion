package com.mena97villalobos.domain.usecases

import com.mena97villalobos.domain.model.Warranty
import com.mena97villalobos.domain.repository.WarrantyRepository

/** Inserts a new warranty row. */
class AddWarrantyUseCase(
    private val repository: WarrantyRepository,
) {
    suspend operator fun invoke(warranty: Warranty) {
        repository.insertWarranty(warranty)
    }
}
