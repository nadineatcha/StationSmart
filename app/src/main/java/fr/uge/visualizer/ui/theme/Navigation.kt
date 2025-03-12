package fr.uge.visualizer.ui.theme

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import fr.uge.visualizer.ui.PredictionScreen
import fr.uge.visualizer.viewmodel.NotificationViewModel
import fr.uge.visualizer.viewmodel.PredictionViewModel

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
    }
}