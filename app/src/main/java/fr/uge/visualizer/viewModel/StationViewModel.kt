// StationViewModel.kt dans fr.uge.visualizer.viewmodel
package fr.uge.visualizer.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import fr.uge.visualizer.location.LocationManager
import fr.uge.visualizer.model.Station
import fr.uge.visualizer.repository.StationRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class StationViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = StationRepository()
    private val locationManager = LocationManager(application.applicationContext)

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _nearbyStations = MutableStateFlow<List<Station>>(emptyList())
    val nearbyStations: StateFlow<List<Station>> = _nearbyStations

    init {
        refreshNearbyStations()
    }

    fun refreshNearbyStations() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // Tenter d'obtenir la position actuelle
                val location = locationManager.getCurrentLocation()

                if (location != null) {
                    Log.d("STATION_VM", "Position obtenue: ${location.latitude}, ${location.longitude}")
                    // Obtenir les stations à proximité
                    val stations = repository.getNearbyStations(location.latitude, location.longitude)

                    // Calculer la distance pour chaque station
                    val stationsWithDistance = stations.map { station ->
                        val distance = locationManager.calculateDistance(
                            location.latitude, location.longitude,
                            station.latitude, station.longitude
                        )
                        station.copy(distance = distance)
                    }

                    // Trier par distance
                    _nearbyStations.value = stationsWithDistance.sortedBy { it.distance }
                    Log.d("STATION_VM", "Stations chargées: ${stationsWithDistance.size}")
                } else {
                    Log.e("STATION_VM", "Impossible d'obtenir la position")
                    // Utiliser des données simulées sans distance
                    val stations = repository.getNearbyStations(0.0, 0.0)
                    _nearbyStations.value = stations
                }
            } catch (e: Exception) {
                Log.e("STATION_VM", "Erreur lors du chargement des stations", e)
                // En cas d'erreur, garder la liste actuelle
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun hasLocationPermission(): Boolean {
        return locationManager.hasLocationPermission()
    }
}

