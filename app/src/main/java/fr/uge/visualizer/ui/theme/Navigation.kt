package fr.uge.visualizer.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import fr.uge.visualizer.ui.PredictionScreen
import fr.uge.visualizer.ui.StationsScreen
import fr.uge.visualizer.viewmodel.NotificationViewModel
import fr.uge.visualizer.viewmodel.PredictionViewModel
import fr.uge.visualizer.viewmodel.StationViewModel


@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val predictionViewModel = PredictionViewModel()
    val notificationViewModel = NotificationViewModel()

    NavHost(navController = navController, startDestination = "predictions") {
        composable("predictions") {
            PredictionScreen(
                viewModel = predictionViewModel,
                onNavigateToNotifications = {
                    navController.navigate("notifications")
                }
            )
        }
        composable("notifications") {
            NotificationScreen(
                viewModel = notificationViewModel,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
        composable("stations") {
            val viewModel = viewModel<StationViewModel>()
            StationsScreen(
                viewModel = viewModel,
                onNavigateBack = {
                    navController.popBackStack()
                },
                onStationSelected = { stationId ->
                    navController.navigate("predictions/$stationId")
                }
            )
        }
        composable(
            "predictions/{stationId}",
            arguments = listOf(navArgument("stationId") { type = NavType.StringType })
        ) { backStackEntry ->
            val stationId = backStackEntry.arguments?.getString("stationId") ?: "default"
            val viewModel = viewModel<PredictionViewModel>()

            // Charger les prédictions au moment de la navigation
            LaunchedEffect(stationId) {
                viewModel.loadStationInfo(stationId)
            }

            PredictionScreen(
                viewModel = viewModel,
                onNavigateToNotifications = { navController.navigate("notifications") },
                onNavigateToStations = { navController.navigate("stations") }
            )
        }
    }
}