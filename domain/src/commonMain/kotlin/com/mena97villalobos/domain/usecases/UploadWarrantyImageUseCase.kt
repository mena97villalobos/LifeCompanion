package com.mena97villalobos.domain.usecases

import com.mena97villalobos.domain.repository.WarrantyRepository

class UploadWarrantyImageUseCase(
    private val repository: WarrantyRepository,
) {
    suspend operator fun invoke(imageUri: String): String = repository.uploadImage(imageUri)
}
