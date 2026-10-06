package com.akhil.aimaze.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.akhil.aimaze.ui.screens.about.AboutScreen
import com.akhil.aimaze.ui.screens.comparison.ComparisonScreen
import com.akhil.aimaze.ui.screens.history.HistoryScreen
import com.akhil.aimaze.ui.screens.home.HomeScreen
import com.akhil.aimaze.ui.screens.play.PlayMazeScreen
import com.akhil.aimaze.ui.screens.settings.SettingsScreen
import com.akhil.aimaze.ui.screens.timeattack.TimeAttackScreen

@Composable
fun AiMazeNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
) {
    NavHost(
        navController = navController,
        startDestination = AppDestination.Home.route,
        modifier = modifier,
    ) {
        composable(AppDestination.Home.route) {
            HomeScreen(
                destinations = AppDestination.homeSections,
                onDestinationSelected = { destination ->
                    navController.navigate(destination.route) {
                        launchSingleTop = true
                    }
                },
            )
        }
        composable(AppDestination.Play.route) {
            PlayMazeScreen(onBack = navController::navigateUp)
        }
        composable(AppDestination.TimeAttack.route) {
            TimeAttackScreen(onBack = navController::navigateUp)
        }
        composable(AppDestination.Comparison.route) {
            ComparisonScreen(onBack = navController::navigateUp)
        }
        composable(AppDestination.History.route) {
            HistoryScreen(onBack = navController::navigateUp)
        }
        composable(AppDestination.Settings.route) {
            SettingsScreen(onBack = navController::navigateUp)
        }
        composable(AppDestination.About.route) {
            AboutScreen(onBack = navController::navigateUp)
        }
    }
}
