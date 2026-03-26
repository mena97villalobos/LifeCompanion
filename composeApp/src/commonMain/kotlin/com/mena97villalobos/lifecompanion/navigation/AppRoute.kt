package com.mena97villalobos.lifecompanion.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

/** Application-level navigation routes used by Navigation 3 back stack entries. */
@Serializable
sealed interface AppRoute : NavKey {
    @Serializable
    data object Home : AppRoute

    @Serializable
    data object Dashboard : AppRoute

    @Serializable
    data object Profile : AppRoute

    @Serializable
    data object WarrantyList : AppRoute

    @Serializable
    data object AddWarranty : AppRoute
}

val bottomTabRoutes: List<AppRoute> = listOf(
    AppRoute.Home,
    AppRoute.Dashboard,
    AppRoute.Profile,
    AppRoute.WarrantyList,
)

val AppRoute.tabIcon: ImageVector
    get() = when (this) {
        AppRoute.Home -> Icons.Default.Home
        AppRoute.Dashboard -> Icons.Default.Favorite
        AppRoute.Profile -> Icons.Default.AccountBox
        AppRoute.WarrantyList -> Icons.Default.Favorite
        AppRoute.AddWarranty -> Icons.Default.Favorite
    }

val AppRoute.tabLabel: String
    get() = when (this) {
        AppRoute.Home -> "Home"
        AppRoute.Dashboard -> "Dashboard"
        AppRoute.Profile -> "Profile"
        AppRoute.WarrantyList -> "Warranties"
        AppRoute.AddWarranty -> "Add Warranty"
    }
