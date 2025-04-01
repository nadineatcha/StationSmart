package fr.uge.visualizer.location

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class LocationManager(private val context: Context) {
    private val fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)

    // Vérifier si les permissions sont accordées
    fun hasLocationPermission(): Boolean {
        return ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
    }

    // Obtenir la dernière position connue
    suspend fun getLastLocation(): Location? {
        if (!hasLocationPermission()) {
            return null
        }

        return try {
            // Vérifier à nouveau les permissions juste avant l'appel
            if (!hasLocationPermission()) {
                throw SecurityException("Location permission is not granted")
            }

            suspendCancellableCoroutine { continuation ->
                try {
                    fusedLocationClient.lastLocation
                        .addOnSuccessListener { location ->
                            continuation.resume(location)
                        }
                        .addOnFailureListener { exception ->
                            continuation.resumeWithException(exception)
                        }
                } catch (e: SecurityException) {
                    continuation.resumeWithException(e)
                }
            }
        } catch (e: Exception) {
            null
        }
    }

    // Obtenir la position actuelle
    suspend fun getCurrentLocation(): Location? {
        if (!hasLocationPermission()) {
            return null
        }

        val cancellationTokenSource = CancellationTokenSource()

        return try {
            // Vérifier à nouveau les permissions juste avant l'appel
            if (!hasLocationPermission()) {
                throw SecurityException("Location permission is not granted")
            }

            suspendCancellableCoroutine { continuation ->
                try {
                    fusedLocationClient.getCurrentLocation(
                        Priority.PRIORITY_HIGH_ACCURACY,
                        cancellationTokenSource.token
                    )
                        .addOnSuccessListener { location ->
                            continuation.resume(location)
                        }
                        .addOnFailureListener { exception ->
                            continuation.resumeWithException(exception)
                        }

                    continuation.invokeOnCancellation {
                        cancellationTokenSource.cancel()
                    }
                } catch (e: SecurityException) {
                    continuation.resumeWithException(e)
                }
            }
        } catch (e: Exception) {
            null
        }
    }

    // Calculer la distance entre deux points
    fun calculateDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Float {
        val results = FloatArray(1)
        Location.distanceBetween(lat1, lon1, lat2, lon2, results)
        return results[0]
    }
}