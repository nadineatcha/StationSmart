package fr.uge.visualizer.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import fr.uge.visualizer.model.HourlyPrediction
import fr.uge.visualizer.model.StationInfo
import fr.uge.visualizer.repository.StationRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class PredictionViewModel : ViewModel() {
    // Création du repository
    private val repository = StationRepository()

    // État pour représenter le chargement, l'erreur ou le succès
    sealed class UiState {
        object Loading : UiState()
        data class Success(val isFallbackData: Boolean = false) : UiState()
        data class Error(val message: String) : UiState()
    }

    private val _uiState = MutableStateFlow<UiState>(UiState.Success(false))
    val uiState: StateFlow<UiState> = _uiState

    // Garde la référence du job de chargement pour pouvoir l'annuler
    private var loadJob: Job? = null

    // Message Toast
    private val _toastMessage = MutableStateFlow<String?>(null)
    val toastMessage: StateFlow<String?> = _toastMessage

    // Cache pour éviter les appels répétés
    private val predictionsCache = mutableMapOf<String, List<HourlyPrediction>>()
    private val stationInfoCache = mutableMapOf<String, StationInfo>()

    // Informations de la station
    private val _stationInfo = MutableStateFlow(
        StationInfo(
            id = "71379",
            name = "PORTE MAILLOT",
            lines = "1, 4, 7, 11, 14",
            currentTraffic = 1250,
            trend = 12,
            peakTraffic = 2100,
            peakTime = "18h30"
        )
    )
    val stationInfo: StateFlow<StationInfo> = _stationInfo

    // Prédictions horaires
    private val _hourlyPredictions = MutableStateFlow<List<HourlyPrediction>>(
        listOf(
            HourlyPrediction("09:00", 500, 30, "up"),
            HourlyPrediction("12:00", 1200, 65, "up"),
            HourlyPrediction("15:00", 800, 45, "down"),
            HourlyPrediction("18:00", 2100, 90, "up"),
            HourlyPrediction("21:00", 600, 35, "down")
        )
    )
    val hourlyPredictions: StateFlow<List<HourlyPrediction>> = _hourlyPredictions

    // Fonction pour charger les prédictions depuis l'API
    fun loadPredictions(stationId: String, date: String? = null, forceRefresh: Boolean = false) {
        // Annule le job précédent si nécessaire
        loadJob?.cancel()

        loadJob = viewModelScope.launch {
            try {
                _uiState.value = UiState.Loading

                // Utiliser la date fournie ou la date actuelle formatée
                val dateToUse = date ?: SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

                // Clé de cache
                val cacheKey = "$stationId-$dateToUse"

                // Vérifier le cache si on ne force pas le rafraîchissement
                if (!forceRefresh && predictionsCache.containsKey(cacheKey)) {
                    Log.d("API_VIEWMODEL", "Utilisation des données en cache pour $cacheKey")
                    _hourlyPredictions.value = predictionsCache[cacheKey] ?: emptyList()
                    _uiState.value = UiState.Success(false)
                    return@launch
                }

                Log.d("API_VIEWMODEL", "Chargement des prédictions pour station $stationId")

                try {
                    // Récupérer les prédictions
                    val predictions = repository.getPredictions(stationId, dateToUse)


                    // Vérifier si on a reçu des données réelles ou des fallbacks
                    val isFallbackData = false||
                            (predictions.size == 4 &&
                                    predictions[0].time == "09:00" &&
                                    predictions[0].count == 500)

                    // Ajouter du logging pour déboguer
                    Log.d("API_VIEWMODEL", "Données reçues - nombre: ${predictions.size}")
                    predictions.forEach { prediction ->
                        Log.d("API_VIEWMODEL", "Prédiction: time=${prediction.time}, count=${prediction.count}, percentageCapacity=${prediction.percentageCapacity}, trend=${prediction.trend}")
                    }

                    // Mettre en cache les résultats
                    if (!isFallbackData) {
                        predictionsCache[cacheKey] = predictions
                    }

                    // Mettre à jour l'état avec une nouvelle liste (pour forcer la mise à jour UI)
                    _hourlyPredictions.value = predictions.toList()
                    _uiState.value = UiState.Success(isFallbackData)

                    if (isFallbackData) {
                        _toastMessage.value = "Données indisponibles, affichage des données de secours"
                    } else {
                        _toastMessage.value = "Chargé ${predictions.size} prédictions"
                    }

                    Log.d("API_VIEWMODEL", "Prédictions chargées avec succès: ${predictions.size}")
                } catch (e: Exception) {
                    Log.e("API_VIEWMODEL", "Erreur lors du chargement des prédictions", e)
                    _uiState.value = UiState.Error(e.message ?: "Erreur inconnue")
                    _toastMessage.value = "Erreur: ${e.message}"
                }
            } catch (ce: CancellationException) {
                // Gestion normale de l'annulation, ne rien faire
                Log.d("API_VIEWMODEL", "Chargement annulé")
            } finally {
                // Réinitialiser le message après 3 secondes
                if (_toastMessage.value != null) {
                    delay(3000)
                    _toastMessage.value = null
                }
            }
        }
    }

    // Fonction pour charger les informations d'une station spécifique
    fun loadStationInfo(stationId: String, forceRefresh: Boolean = false) {
        viewModelScope.launch {
            try {
                _uiState.value = UiState.Loading

                // Vérifier le cache si on ne force pas le rafraîchissement
                if (!forceRefresh && stationInfoCache.containsKey(stationId)) {
                    Log.d("API_VIEWMODEL", "Utilisation des infos de station en cache pour $stationId")
                    _stationInfo.value = stationInfoCache[stationId]!!
                } else {
                    try {
                        val info = repository.getStationInfo(stationId)
                        stationInfoCache[stationId] = info
                        _stationInfo.value = info
                        Log.d("API_PROCESSED", "Trafic actuel: ${info.currentTraffic}, Pic: ${info.peakTraffic}")
                    } catch (e: Exception) {
                        Log.e("API_VIEWMODEL", "Erreur lors du chargement des infos de station", e)
                        // En cas d'erreur, on garde les infos existantes mais on affiche un message
                        _toastMessage.value = "Erreur de chargement des infos de station: ${e.message}"
                    }
                }

                // Après avoir chargé les infos de la station, on charge aussi les prédictions
                loadPredictions(stationId, forceRefresh = forceRefresh)
            } catch (e: Exception) {
                // Gérer l'erreur
                _uiState.value = UiState.Error(e.message ?: "Erreur inconnue")
                _toastMessage.value = "Erreur: ${e.message}"
            }
        }
    }

    // Fonction pour recharger les données actuelles
    fun refresh() {
        val currentStationId = _stationInfo.value.id
        loadStationInfo(currentStationId, forceRefresh = true)
    }

    override fun onCleared() {
        super.onCleared()
        // Annuler tous les jobs en cours lorsque le ViewModel est détruit
        loadJob?.cancel()
    }
}