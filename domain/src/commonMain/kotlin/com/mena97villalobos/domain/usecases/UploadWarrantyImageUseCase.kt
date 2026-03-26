package com.mena97villalobos.domain.usecases

import com.mena97villalobos.domain.repository.WarrantyRepository

/** Uploads a warranty image and returns the object id to store on the warranty. */
class UploadWarrantyImageUseCase(
    private val repository: WarrantyRepository,
) {
    suspend operator fun invoke(imageUri: String): String = repository.uploadImage(imageUri)
}
