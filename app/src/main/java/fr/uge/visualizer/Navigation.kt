package fr.uge.visualizer

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

@Composable
fun Navigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "splash_screen") {
        composable("splash_screen") {
            SplashScreen(navController)
        }
        composable("main_screen") {
            MainScreen(navController)
        }
        // Ajoutez d'autres écrans ici au fur et à mesure
    }
}