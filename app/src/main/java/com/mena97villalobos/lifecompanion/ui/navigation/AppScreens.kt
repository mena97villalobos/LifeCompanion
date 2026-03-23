package com.mena97villalobos.lifecompanion.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.ui.graphics.vector.ImageVector

sealed class AppScreens(
    val route: String,
    val label: String,
    val icon: ImageVector,
) {
    data object Home : AppScreens(
        "home",
        "Home",
        Icons.Default.Home,
    )

    data object Dashboard : AppScreens(
        "dashboard",
        "Dashboard",
        Icons.Default.Favorite,
    )

    data object Profile : AppScreens(
        "profile",
        "Profile",
        Icons.Default.AccountBox,
    )

    data object WarrantyList : AppScreens(
        "warranty_list",
        "Warranties",
        Icons.Default.Favorite,
    )

    data object AddWarranty : AppScreens(
        "add_warranty",
        "Add Warranty",
        Icons.Default.Favorite,
    )

    companion object {
        val entries = listOf(Home, Dashboard, Profile, WarrantyList)
    }
}
