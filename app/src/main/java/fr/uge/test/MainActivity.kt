// MainActivity.kt
package fr.uge.test

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.QueryStats
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import fr.uge.test.ui.theme.MainScreen
import fr.uge.test.ui.theme.SplashScreen
import fr.uge.test.ui.theme.TermsScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppTheme {
                AppNavigation()
            }
        }
    }
}

@Composable
fun AppTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colors = lightColors(
            primary = Color(0xFF4F46E5),
            primaryVariant = Color(0xFF3730A3),
            secondary = Color(0xFF10B981)
        ),
        content = content
    )
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = currentBackStackEntry?.destination

    val showBottomBar = when(currentDestination?.route) {
        "splash_screen" -> false
        else -> true
    }

    Scaffold(
        bottomBar = { if(showBottomBar) BottomNavigationBar(navController) }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "splash_screen",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("splash_screen") {
                SplashScreen(navController)
            }
            composable("Terms") {
                TermsScreen(
                    onNextClicked = { navController.navigate(Screen.Home.route) }
                )
            }
            composable(Screen.Home.route) {
                MainScreen(Modifier.padding(innerPadding))
            }
            composable(Screen.Predicitions.route) {
                PlaceholderScreen("Prédiction")
            }
            composable(Screen.Settings.route) {
                PlaceholderScreen("Paramètres")
            }
        }
    }
}

sealed class Screen(val route: String, val label: String, val icon: ImageVector) {
    object Home : Screen("main_screen", "Accueil", Icons.Default.Home)
    //object Predicitions : Screen("prédicition", "Predécition", Icons.Default.Notifications)
    object Predicitions : Screen("prédicition", "Prédiction", Icons.Default.QueryStats)

    object Settings : Screen("settings", "Paramètres", Icons.Default.Settings)
    //object Terms : Screen("terms_screen", "Conditions", Icons.Default.Description)

}


@Composable
fun BottomNavigationBar(navController: NavController) {
    val items = listOf(Screen.Home, Screen.Predicitions, Screen.Settings)

    BottomNavigation {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentDestination = navBackStackEntry?.destination

        items.forEach { screen ->
            BottomNavigationItem(
                icon = { Icon(screen.icon, contentDescription = screen.label) },
                label = { Text(screen.label) },
                selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                onClick = {
                    navController.navigate(screen.route) {
                        popUpTo(navController.graph.startDestinationId) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    }
}

@Composable
fun PlaceholderScreen(title: String) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(title, fontSize = 24.sp)
    }
}