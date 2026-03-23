package com.mena97villalobos.domain.usecases

import android.net.Uri
import com.mena97villalobos.domain.repository.WarrantyRepository

class UploadWarrantyImageUseCase(
    private val repository: WarrantyRepository,
) {
    suspend operator fun invoke(uri: Uri): String = repository.uploadImage(uri)
}
