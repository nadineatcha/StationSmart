package fr.uge.test.viewmodel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import fr.uge.test.model.HourlyPrediction
import fr.uge.test.model.Station
import fr.uge.test.model.StationInfo
import fr.uge.test.model.TrafficLevel
import fr.uge.test.repository.StationRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch



class StationViewModel : ViewModel() {
    private val repository = StationRepository()

    // État pour toutes les stations
    private val _allStations = MutableStateFlow<List<Station>>(emptyList())
    val allStations: StateFlow<List<Station>> = _allStations

    // État pour les stations filtrées par recherche
    private val _searchResults = MutableStateFlow<List<Station>>(emptyList())
    val searchResults: StateFlow<List<Station>> = _searchResults.asStateFlow()

    // État pour les stations à proximité
    private val _nearbyStations = MutableStateFlow<List<Station>>(emptyList())
    val nearbyStations: StateFlow<List<Station>> = _nearbyStations

    // État pour la station sélectionnée
    private val _selectedStation = MutableStateFlow<Station?>(null)
    val selectedStation: StateFlow<Station?> = _selectedStation

    // État pour les informations de la station sélectionnée
    private val _stationInfo = MutableStateFlow<StationInfo?>(null)
    val stationInfo: StateFlow<StationInfo?> = _stationInfo

    // État pour les prédictions de la station sélectionnée
    private val _predictions = MutableStateFlow<List<HourlyPrediction>>(emptyList())
    val predictions: StateFlow<List<HourlyPrediction>> = _predictions



    // État de chargement
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    // État d'erreur
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    init {
        // Charger toutes les stations au démarrage
        loadAllStations()
    }

    init {
        loadAllStations()
    }

    fun loadAllStations() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val stations = repository.getAllStations()
                _allStations.value = stations
                _searchResults.value = stations // Mise à jour cruciale
            } catch (e: Exception) {
                _error.value = "Erreur: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Fonction pour rechercher des stations
    fun searchStations(query: String) {
        if (query.isBlank()) {
            _searchResults.value = _allStations.value
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            try {
                val results = repository.searchStations(query)
                _searchResults.value = results
            } catch (e: Exception) {
                _error.value = "Erreur lors de la recherche: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Fonction pour charger les stations à proximité
    fun loadNearbyStations(latitude: Double, longitude: Double) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val stations = repository.getNearbyStations(latitude, longitude)
                _nearbyStations.value = stations
            } catch (e: Exception) {
                _error.value = "Erreur lors du chargement des stations à proximité: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }


    // Fonction pour sélectionner une station
    fun selectStation(station: Station) {
        _selectedStation.value = station
        loadStationDetails(station.id)
    }

    // Fonction pour charger les détails d'une station
    fun loadStationDetails(stationId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // Charger les informations de la station
                val info = repository.getStationInfo(stationId)
                _stationInfo.value = info

                // Charger les prédictions
                val predictions = repository.getPredictions(stationId)
                _predictions.value = predictions
            } catch (e: Exception) {
                _error.value = "Erreur lors du chargement des détails: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Fonction pour obtenir le niveau de trafic d'une station
    fun getTrafficLevel(station: Station): TrafficLevel {
        return if (station.currentTraffic > 0) { // Vérifier que la valeur est valide
            TrafficLevel.fromTraffic(station.currentTraffic)
        } else {
            TrafficLevel.MEDIUM // Valeur par défaut sécurisée
        }
    }
    // Réinitialiser les erreurs
    fun clearError() {
        _error.value = null
    }
}
