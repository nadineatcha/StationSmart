package fr.uge.test

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import fr.uge.test.ui.theme.*
import fr.uge.test.viewmodel.NotificationViewModel
import fr.uge.test.viewmodel.PredictionViewModel
import fr.uge.test.viewmodel.StationViewModel
import fr.uge.test.model.Station
import androidx.compose.material.icons.automirrored.filled.CompareArrows

class MainActivity : ComponentActivity() {

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        // Aucune action particulière après l'obtention des permissions
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        checkAndRequestLocationPermissions()

        setContent {
            AppTheme {
                AppNavigation()
            }
        }
    }

    private fun checkAndRequestLocationPermissions() {
        val hasFineLocationPermission = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        val hasCoarseLocationPermission = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        if (!hasFineLocationPermission || !hasCoarseLocationPermission) {
            requestPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
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
fun StationItem(
    station: Station,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable(onClick = onClick),
        elevation = 4.dp
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = station.name,
                    style = MaterialTheme.typography.h6
                )
                Text(
                    text = "Lignes: ${station.lines}",
                    style = MaterialTheme.typography.body2
                )
            }
        }
    }
}

@Composable
fun NearbyStationsScreen(
    viewModel: StationViewModel,
    onNavigateBack: () -> Unit,
    onStationSelected: (String) -> Unit
) {
    val stations by viewModel.nearbyStations.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Stations à proximité") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Retour")
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.loadNearbyStations(0.0, 0.0) }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Rafraîchir")
                    }
                },
                backgroundColor = Color(0xFF4F46E5),
                contentColor = Color.White
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = Color(0xFF4F46E5)
                )
            } else {
                LazyColumn {
                    items(stations) { station ->
                        StationItem(
                            station = station,
                            onClick = { onStationSelected(station.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = currentBackStackEntry?.destination

    val showBottomBar = when(currentDestination?.route) {
        "splash_screen", "notifications", "stations", "nearby_stations",
        "conditions", "terms", "confidentiality", "traffic_alert", "legal" -> false
        "comparaison" -> false
        else -> true
    }

    Scaffold(
        bottomBar = { if(showBottomBar) BottomNavigationBar(navController) }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "splash_screen",
            modifier = Modifier.padding(innerPadding)
        )
        {
            composable("comparaison") {
                val viewModel: StationViewModel = viewModel()
                ComparaisonScreen(
                    //viewModel = viewModel,
                    onNavigateBack = { navController.popBackStack() }
                )
            }
            composable("splash_screen") {
                SplashScreen(navController)
            }

            composable("terms") {
                TermsScreen(
                    onNextClicked = { navController.navigate(Screen.Home.route) {
                        popUpTo("splash_screen") { inclusive = true }
                    }}
                )
            }



            composable(Screen.Home.route) {
                MainScreen(
                    modifier = Modifier.padding(innerPadding),
                    navController = navController
                )
            }

            composable(Screen.Predicitions.route) {
                val predictionViewModel: PredictionViewModel = viewModel()
                PredictionScreen(
                    viewModel = predictionViewModel,
                    onNavigateToNotifications = { navController.navigate("notifications") },
                    onNavigateToStations = { navController.navigate("stations") },
                    onNavigateToLegal = { navController.navigate("legal") }
                )
            }

            composable(Screen.Comparaison.route) {
                ComparaisonScreen(
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable("notifications") {
                val notificationViewModel: NotificationViewModel = viewModel()
                NotificationScreen(
                    viewModel = notificationViewModel,
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable("stations") {
                StationsScreen(onNavigateBack = { navController.popBackStack() })
            }

            composable("nearby_stations") {
                val viewModel: StationViewModel = viewModel()
                NearbyStationsScreen(
                    viewModel = viewModel,
                    onNavigateBack = { navController.popBackStack() },
                    onStationSelected = { stationId ->
                        navController.navigate("prediction/$stationId")
                    }
                )
            }

            composable("legal") {
                LegalScreen(onNavigateBack = { navController.popBackStack() })
            }

            composable(Screen.Settings.route) {
                AppSettingsScreen(navController)
            }

            composable("articles") {
                ArticlesScreen(navController)
            }

            composable("confidentiality") {
                ConfidentialiteScreen(navController)
            }

            composable("conditions") {
                ConditionScreen(navController)
            }

            composable("traffic_alert") {
                TraficAlertPage(navController)
            }

            composable(
                "prediction/{stationId}",
                arguments = listOf(navArgument("stationId") { type = NavType.StringType })
            ) { backStackEntry ->
                val stationId = backStackEntry.arguments?.getString("stationId") ?: "default"
                val viewModel: PredictionViewModel = viewModel()

                LaunchedEffect(stationId) {
                    viewModel.loadStationInfo(stationId)
                }

                PredictionScreen(
                    viewModel = viewModel,
                    onNavigateToNotifications = { navController.navigate("notifications") },
                    onNavigateToStations = { navController.navigate("stations") },
                    onNavigateToLegal = { navController.navigate("legal") }
                )
            }
        }
    }
}

sealed class Screen(val route: String, val label: String, val icon: ImageVector) {
    object Home : Screen("main_screen", "Accueil", Icons.Default.Home)
    object Predicitions : Screen("prediction", "Prédiction", Icons.Default.QueryStats)
    object Settings : Screen("settings", "Paramètres", Icons.Default.Settings)
    object Comparaison : Screen("comparaison", "Comparer", Icons.AutoMirrored.Filled.CompareArrows)

}

@Composable
fun BottomNavigationBar(navController: NavController) {
    val items = listOf(Screen.Home, Screen.Predicitions,Screen.Comparaison, Screen.Settings)

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
fun StationsScreen(onNavigateBack: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Stations à proximité") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Retour")
                    }
                },
                backgroundColor = Color(0xFF4F46E5),
                contentColor = Color.White
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            Text("Liste des stations à proximité (en développement)")
        }
    }
}

@Composable
fun LegalScreen(onNavigateBack: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Informations légales") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Retour")
                    }
                },
                backgroundColor = Color(0xFF4F46E5),
                contentColor = Color.White
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            Text("Informations légales et mentions légales de l'application")
        }
    }
}

@Composable
fun PlaceholderScreen(title: String) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(title, fontSize = 24.sp)
    }
}