package com.mena97villalobos.lifecompanion.ui.warranty.add

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mena97villalobos.domain.model.Warranty
import com.mena97villalobos.domain.usecases.AddWarrantyUseCase
import com.mena97villalobos.domain.usecases.UpdateWarrantyUseCase
import com.mena97villalobos.domain.usecases.UploadWarrantyImageUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate

class AddEditWarrantyViewModel(
    private val addWarranty: AddWarrantyUseCase,
    private val updateWarranty: UpdateWarrantyUseCase,
    private val uploadImage: UploadWarrantyImageUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(WarrantyFormState())
    val state: StateFlow<WarrantyFormState> = _state.asStateFlow()

    fun handleIntent(intent: WarrantyFormIntent) {
        when (intent) {
            is WarrantyFormIntent.DescriptionChanged -> _state.update { it.copy(description = intent.description) }
            is WarrantyFormIntent.StoreChanged -> _state.update { it.copy(storeName = intent.store) }
            is WarrantyFormIntent.PurchaseDateChanged -> _state.update { it.copy(purchaseDate = intent.date) }
            is WarrantyFormIntent.ExpiryDateChanged -> _state.update { it.copy(expiryDate = intent.date) }
            is WarrantyFormIntent.NotesChanged -> _state.update { it.copy(notes = intent.notes) }
            is WarrantyFormIntent.ImageSelected -> _state.update { it.copy(imageUri = intent.uri) }
            WarrantyFormIntent.Save -> saveWarranty()
        }
    }

    fun loadWarranty(warranty: Warranty) {
        _state.update {
            it.copy(
                id = warranty.id,
                description = warranty.description,
                storeName = warranty.storeName,
                purchaseDate = warranty.purchaseDate,
                expiryDate = warranty.expiryDate,
                notes = warranty.notes ?: "",
                imageObjectId = warranty.imageObjectId,
                isEditMode = true,
            )
        }
    }

    private fun saveWarranty() {
        val current = _state.value
        if (current.isFormValid.not()) {
            _state.update {
                it.copy(error = "Please fill all required fields")
            }
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(isSaving = true) }
            try {
                var imageObjectId = current.imageObjectId
                if (current.imageUri != null) {
                    imageObjectId = uploadImage(current.imageUri.toString())
                }
                val purchaseDate = current.purchaseDate
                val expiryDate = current.expiryDate
                if (purchaseDate == null || expiryDate == null) {
                    _state.update {
                        it.copy(
                            isSaving = false,
                            error = "Please provide valid dates",
                        )
                    }
                    return@launch
                }

                val warranty = Warranty(
                    id = current.id,
                    description = current.description,
                    storeName = current.storeName,
                    purchaseDate = purchaseDate,
                    expiryDate = expiryDate,
                    notes = current.notes,
                    imageObjectId = imageObjectId,
                )
                if (current.isEditMode) {
                    updateWarranty(warranty)
                } else {
                    addWarranty(warranty)
                }
                _state.update {
                    it.copy(
                        isSaving = false,
                    )
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        isSaving = false,
                        error = e.message,
                    )
                }
            }
        }
    }
}

data class WarrantyFormState(
    val id: Long? = null,
    val description: String = "",
    val storeName: String = "",
    val purchaseDate: LocalDate? = null,
    val expiryDate: LocalDate? = null,
    val notes: String = "",
    val imageUri: Uri? = null,
    val imageObjectId: String? = null,
    val isSaving: Boolean = false,
    val error: String? = null,
    val isEditMode: Boolean = false,
) {
    val isFormValid: Boolean
        get() = description.isNotBlank() &&
            storeName.isNotBlank() &&
            purchaseDate != null &&
            expiryDate != null
}

sealed class WarrantyFormIntent {

    data class DescriptionChanged(
        val description: String,
    ) : WarrantyFormIntent()

    data class StoreChanged(
        val store: String,
    ) : WarrantyFormIntent()

    data class PurchaseDateChanged(
        val date: LocalDate?,
    ) : WarrantyFormIntent()

    data class ExpiryDateChanged(
        val date: LocalDate?,
    ) : WarrantyFormIntent()

    data class NotesChanged(
        val notes: String,
    ) : WarrantyFormIntent()

    data class ImageSelected(
        val uri: Uri,
    ) : WarrantyFormIntent()

    object Save : WarrantyFormIntent()
}
