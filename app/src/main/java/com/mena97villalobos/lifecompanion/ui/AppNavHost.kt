package com.mena97villalobos.lifecompanion.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.mena97villalobos.lifecompanion.ui.dashboard.DashboardScreen
import com.mena97villalobos.lifecompanion.ui.home.HomeScreen
import com.mena97villalobos.lifecompanion.ui.navigation.AppScreens
import com.mena97villalobos.lifecompanion.ui.warranty.add.AddEditWarrantyScreen
import com.mena97villalobos.lifecompanion.ui.warranty.list.WarrantyListScreen

@Composable
fun AppNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
) {
    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = AppScreens.Home.route,
    ) {
        composable(AppScreens.Home.route) {
            HomeScreen()
        }

        composable(AppScreens.Dashboard.route) {
            DashboardScreen(navController)
        }

        composable(AppScreens.Profile.route) {
            HomeScreen()
        }

        composable(AppScreens.WarrantyList.route) {
            WarrantyListScreen(
                onAddClick = {
                    navController.navigate(AppScreens.AddWarranty.route)
                },
                onEditClick = {
                    navController.navigate(AppScreens.AddWarranty.route)
                },
            )
        }

        composable(AppScreens.AddWarranty.route) {
            AddEditWarrantyScreen()
        }
    }
}
