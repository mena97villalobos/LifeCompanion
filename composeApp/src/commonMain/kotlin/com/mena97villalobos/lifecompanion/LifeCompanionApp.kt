package com.mena97villalobos.lifecompanion

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.ui.NavDisplay
import com.mena97villalobos.designsystem.theme.LifeCompanionTheme
import com.mena97villalobos.lifecompanion.navigation.AppRoute
import com.mena97villalobos.lifecompanion.navigation.bottomTabRoutes
import com.mena97villalobos.lifecompanion.navigation.tabIcon
import com.mena97villalobos.lifecompanion.navigation.tabLabel
import com.mena97villalobos.lifecompanion.ui.dashboard.DashboardScreen
import com.mena97villalobos.lifecompanion.ui.home.HomeScreen
import com.mena97villalobos.lifecompanion.ui.lock.LockScreen
import com.mena97villalobos.lifecompanion.ui.onboarding.OnboardingScreen
import com.mena97villalobos.lifecompanion.ui.root.RootUiState
import com.mena97villalobos.lifecompanion.ui.root.RootViewModel
import com.mena97villalobos.lifecompanion.ui.settings.SettingsScreen
import com.mena97villalobos.lifecompanion.ui.warranty.add.AddEditWarrantyScreen
import com.mena97villalobos.lifecompanion.ui.warranty.list.WarrantyListScreen
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun LifeCompanionApp() {
    LifeCompanionTheme {
        LifeCompanionRoot()
    }
}

/**
 * Top-level gate (issue #7): runs onboarding on first launch, shows the lock screen while locked,
 * and only renders the main app — the sensitive screens — once unlocked.
 */
@Composable
private fun LifeCompanionRoot(
    rootViewModel: RootViewModel = koinViewModel(),
) {
    val rootState by rootViewModel.uiState.collectAsStateWithLifecycle()

    when (rootState) {
        RootUiState.Loading -> LoadingScreen()
        RootUiState.Onboarding -> OnboardingScreen()
        RootUiState.Locked -> LockScreen()
        RootUiState.Unlocked -> LifeCompanionNavScaffold()
    }
}

@Composable
private fun LoadingScreen() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }
}

@Composable
private fun LifeCompanionNavScaffold() {
    val backStack =
        remember {
            mutableStateListOf<AppRoute>(AppRoute.Home)
        }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            BottomNavigationBar(
                backStack = backStack,
            )
        },
    ) { innerPadding ->
        NavDisplay(
            backStack = backStack,
            onBack = { backStack.removeLastOrNull() },
            modifier = Modifier.padding(innerPadding),
            entryProvider = { key ->
                when (key) {
                    AppRoute.Home ->
                        NavEntry(key) {
                            HomeScreen()
                        }

                    AppRoute.Dashboard ->
                        NavEntry(key) {
                            DashboardScreen()
                        }

                    AppRoute.Profile ->
                        NavEntry(key) {
                            SettingsScreen()
                        }

                    AppRoute.WarrantyList ->
                        NavEntry(key) {
                            WarrantyListScreen(
                                onAddClick = { backStack.add(AppRoute.AddWarranty) },
                                onEditClick = { backStack.add(AppRoute.AddWarranty) },
                            )
                        }

                    AppRoute.AddWarranty ->
                        NavEntry(key) {
                            AddEditWarrantyScreen()
                        }
                }
            },
        )
    }
}

@Composable
private fun BottomNavigationBar(
    backStack: SnapshotStateList<AppRoute>,
) {
    val current = backStack.lastOrNull() ?: AppRoute.Home
    val selectedTab =
        when (current) {
            AppRoute.AddWarranty -> AppRoute.WarrantyList
            else -> current
        }

    NavigationBar {
        bottomTabRoutes.forEach { tab ->
            val selected = selectedTab == tab
            NavigationBarItem(
                icon = {
                    Icon(
                        tab.tabIcon,
                        contentDescription = tab.tabLabel,
                    )
                },
                label = { Text(tab.tabLabel) },
                selected = selected,
                onClick = {
                    switchTab(backStack, tab)
                },
            )
        }
    }
}

private fun switchTab(
    backStack: SnapshotStateList<AppRoute>,
    tab: AppRoute,
) {
    backStack.clear()
    backStack.add(tab)
}
