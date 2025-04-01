package fr.uge.visualizer.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import fr.uge.visualizer.model.HourlyPrediction
import fr.uge.visualizer.model.StationInfo
import fr.uge.visualizer.repository.StationRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class PredictionViewModel : ViewModel() {
    private val repository = StationRepository()

    // États de l'interface utilisateur
    sealed class UiState {
        object Loading : UiState()
        data class Success(val isFallbackData: Boolean = false) : UiState()
        data class Error(val message: String) : UiState()
    }

    private val _uiState = MutableStateFlow<UiState>(UiState.Success(false))
    val uiState: StateFlow<UiState> = _uiState

    private val _toastMessage = MutableStateFlow<String?>(null)
    val toastMessage: StateFlow<String?> = _toastMessage

    // Caches
    private val predictionsCache = mutableMapOf<String, List<HourlyPrediction>>()
    private val stationInfoCache = mutableMapOf<String, StationInfo>()

    // Station par défaut
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

    // Prédictions par défaut
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

    // Chargement des prédictions
    fun loadPredictions(stationId: String, date: String? = null, forceRefresh: Boolean = false) {
        viewModelScope.launch {
            try {
                _uiState.value = UiState.Loading

                val dateToUse = date ?: SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
                val cacheKey = "$stationId-$dateToUse"

                // Vérification du cache
                val cachedPredictions = predictionsCache[cacheKey]
                if (!forceRefresh && cachedPredictions != null) {
                    _hourlyPredictions.value = cachedPredictions
                    _uiState.value = UiState.Success(false)
                    return@launch
                }

                // Récupération des prédictions
                val predictions = repository.getPredictions(stationId, dateToUse)

                // Détection des données de secours
                val isFallbackData = predictions.size == 4 &&
                        predictions[0].time == "09:00" &&
                        predictions[0].count == 500

                // Mise en cache
                if (!isFallbackData) {
                    predictionsCache[cacheKey] = predictions
                }

                // Mise à jour de l'état
                _hourlyPredictions.value = predictions
                _uiState.value = UiState.Success(isFallbackData)

                _toastMessage.value = if (isFallbackData) {
                    "Données indisponibles, affichage des données de secours"
                } else {
                    "Chargé ${predictions.size} prédictions"
                }

            } catch (e: Exception) {
                _uiState.value = UiState.Error(e.message ?: "Erreur inconnue")
                _toastMessage.value = "Erreur: ${e.message}"
            }
        }
    }

    // Chargement des informations de station
    fun loadStationInfo(stationId: String, forceRefresh: Boolean = false) {
        viewModelScope.launch {
            try {
                _uiState.value = UiState.Loading

                // Vérification du cache
                val cachedStationInfo = stationInfoCache[stationId]
                if (!forceRefresh && cachedStationInfo != null) {
                    _stationInfo.value = cachedStationInfo
                } else {
                    // Récupération des informations de station
                    val info = repository.getStationInfo(stationId)
                    stationInfoCache[stationId] = info
                    _stationInfo.value = info
                }

                // Chargement des prédictions
                loadPredictions(stationId, forceRefresh = forceRefresh)

            } catch (e: Exception) {
                _uiState.value = UiState.Error(e.message ?: "Erreur inconnue")
                _toastMessage.value = "Erreur: ${e.message}"
            }
        }
    }

    // Rafraîchissement des données
    fun refresh() {
        val currentStationId = _stationInfo.value.id
        loadStationInfo(currentStationId, forceRefresh = true)
    }
}