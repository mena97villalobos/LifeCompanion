package com.mena97villalobos.lifecompanion.ui.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mena97villalobos.domain.model.ExchangeRate
import com.mena97villalobos.domain.usecases.GetBuyRateUseCase
import com.mena97villalobos.domain.usecases.GetSellRateUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class DashboardViewModel(
    private val getSellRateUseCase: GetSellRateUseCase,
    private val getBuyRateUseCase: GetBuyRateUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow<DashboardStatus>(DashboardStatus.Loading)
    val uiState: StateFlow<DashboardStatus> = _uiState

    init {
        viewModelScope.launch {
            try {
                _uiState.update {
                    DashboardStatus.Success(
                        getSellRateUseCase(),
                        getBuyRateUseCase(),
                    )
                }
            } catch (_: Exception) {
                _uiState.update {
                    DashboardStatus.Error
                }
            }
        }
    }
}

sealed class DashboardStatus {
    data object Loading : DashboardStatus()
    data class Success(
        val sellExchangeRate: ExchangeRate,
        val buyExchangeRate: ExchangeRate,
    ) : DashboardStatus()
    data object Error : DashboardStatus()
}
