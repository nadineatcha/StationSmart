package fr.uge.visualizer

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import fr.uge.visualizer.ui.theme.AppNavigation
import fr.uge.visualizer.ui.theme.VisualizerTheme

class MainActivity : ComponentActivity() {

    // Déclaration du launcher pour demander les permissions
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        // Aucune action particulière à faire après l'obtention des permissions
        // Le code utilisant la localisation vérifiera les permissions au moment nécessaire
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Vérifier et demander les permissions de localisation
        checkAndRequestLocationPermissions()

        setContent {
            VisualizerTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // Utiliser le système de navigation AppNavigation existant
                    // qui contient déjà les routes pour vos différents écrans
                    AppNavigation()
                }
            }
        }
    }

    // Fonction pour vérifier et demander les permissions
    private fun checkAndRequestLocationPermissions() {
        val hasFineLocationPermission = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        val hasCoarseLocationPermission = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        // Si les permissions ne sont pas accordées, les demander
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