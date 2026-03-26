package com.mena97villalobos.lifecompanion.ui.warranty.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mena97villalobos.domain.model.Warranty
import com.mena97villalobos.domain.usecases.DeleteWarrantyUseCase
import com.mena97villalobos.domain.usecases.GetWarrantiesUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/** Observes warranties, applies query filtering state, and handles delete intents. */
class WarrantyListViewModel(
    private val getWarranties: GetWarrantiesUseCase,
    private val deleteWarranty: DeleteWarrantyUseCase,
) : ViewModel() {
    private val _state = MutableStateFlow(WarrantyListState())
    val state = _state.asStateFlow()

    init {
        observeWarranties()
    }

    private fun observeWarranties() {
        viewModelScope.launch {
            getWarranties().collect { list ->
                _state.update { it.copy(warranties = list) }
            }
        }
    }

    fun handleIntent(intent: WarrantyListIntent) {
        when (intent) {
            is WarrantyListIntent.Search -> {
                _state.update { it.copy(searchQuery = intent.query) }
            }

            is WarrantyListIntent.Delete -> {
                viewModelScope.launch {
                    deleteWarranty(intent.warrantyId)
                }
            }

            is WarrantyListIntent.WarrantyClicked -> Unit

            WarrantyListIntent.AddWarrantyClicked -> Unit
        }
    }
}

data class WarrantyListState(
    val warranties: List<Warranty> = emptyList(),
    val searchQuery: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
)

/** UI intents for warranty list interactions. */
sealed class WarrantyListIntent {
    data class Search(val query: String) : WarrantyListIntent()

    data class Delete(val warrantyId: Long) : WarrantyListIntent()

    data class WarrantyClicked(val warrantyId: Long) : WarrantyListIntent()

    data object AddWarrantyClicked : WarrantyListIntent()
}
