package com.mena97villalobos.lifecompanion.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.mena97villalobos.designsystem.theme.LifeCompanionTheme
import com.mena97villalobos.lifecompanion.ui.navigation.AppScreens

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LifeCompanionTheme {
                LifeCompanionApp()
            }
        }
    }
}

@PreviewScreenSizes
@Composable
private fun LifeCompanionApp() {
    val navController = rememberNavController()

    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry?.destination?.route

    NavigationSuiteScaffold(
        navigationSuiteItems = {
            AppScreens.entries.forEach { screen ->
                item(
                    icon = {
                        Icon(
                            screen.icon,
                            contentDescription = screen.label,
                        )
                    },
                    label = { Text(screen.label) },
                    selected = currentRoute == screen.route,
                    onClick = {
                        navController.navigate(screen.route) {
                            popUpTo(navController.graph.startDestinationId)
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                )
            }
        },
    ) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
        ) { innerPadding ->

            AppNavHost(
                navController = navController,
                modifier = Modifier.padding(innerPadding),
            )
        }
    }
}
