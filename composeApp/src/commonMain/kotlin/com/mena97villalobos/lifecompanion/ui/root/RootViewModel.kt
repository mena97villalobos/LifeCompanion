package com.mena97villalobos.lifecompanion.ui.root

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mena97villalobos.domain.model.AppLockState
import com.mena97villalobos.domain.security.AppLockManager
import com.mena97villalobos.domain.usecases.ObserveProfileUseCase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/** Top-level gate: decides whether to show onboarding, the lock screen, or the unlocked app. */
sealed interface RootUiState {
    data object Loading : RootUiState

    /** No local profile yet — run the first-launch onboarding wizard. */
    data object Onboarding : RootUiState

    /** Profile exists and a lock is engaged — show the lock screen. */
    data object Locked : RootUiState

    /** Authenticated (or no lock configured) — sensitive screens may render. */
    data object Unlocked : RootUiState
}

class RootViewModel(
    observeProfile: ObserveProfileUseCase,
    private val appLockManager: AppLockManager,
) : ViewModel() {

    val uiState: StateFlow<RootUiState> =
        combine(observeProfile(), appLockManager.state) { profile, lockState ->
            when {
                profile == null -> RootUiState.Onboarding
                lockState is AppLockState.Unlocked -> RootUiState.Unlocked
                lockState is AppLockState.NotConfigured -> RootUiState.Unlocked
                else -> RootUiState.Locked
            }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS), RootUiState.Loading)

    init {
        viewModelScope.launch { appLockManager.initialize() }
    }

    /** Re-engage the lock (e.g. on backgrounding or inactivity timeout). */
    fun lock() {
        viewModelScope.launch { appLockManager.lock() }
    }

    private companion object {
        const val STOP_TIMEOUT_MILLIS = 5_000L
    }
}
