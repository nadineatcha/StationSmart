package fr.uge.visualizer.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import fr.uge.visualizer.MainScreen
import fr.uge.visualizer.ui.PredictionScreen
import fr.uge.visualizer.ui.StationsScreen
import fr.uge.visualizer.viewmodel.NotificationViewModel
import fr.uge.visualizer.viewmodel.PredictionViewModel
import fr.uge.visualizer.viewmodel.StationViewModel

// Import pour NotificationScreen (ajustez selon l'emplacement réel)
import fr.uge.visualizer.ui.theme.NotificationScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val predictionViewModel = PredictionViewModel()
    val notificationViewModel = NotificationViewModel()

    // Définir la séquence de navigation : MainScreen -> Predictions -> Notifications
    NavHost(navController = navController, startDestination = "main_screen") {
        // 1. MainScreen - votre écran d'accueil et des conditions légales
        composable("main_screen") {
            MainScreen(navController = navController)
            // Note: Votre MainScreen navigue vers "predictions" après acceptation des conditions
        }

        // 2. Écran principal de prédiction
        composable("predictions") {
            PredictionScreen(
                viewModel = predictionViewModel,
                onNavigateToNotifications = {
                    navController.navigate("notifications")
                },
                onNavigateToStations = {
                    navController.navigate("stations")
                },
                onNavigateToLegal = {
                    navController.navigate("main_screen")
                }
            )
        }

        // 3. Écran des notifications
        composable("notifications") {
            NotificationScreen(
                viewModel = notificationViewModel,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        // Écran des stations
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

        // Écran des prédictions pour une station spécifique
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
                onNavigateToStations = { navController.navigate("stations") },
                onNavigateToLegal = { navController.navigate("main_screen") }
            )
        }
    }
}