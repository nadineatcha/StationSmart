package fr.uge.test.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import fr.uge.test.model.HourlyPrediction
import fr.uge.test.model.StationInfo
import fr.uge.test.repository.StationRepository
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

    // Station par défaut - adaptation aux propriétés utilisées dans l'UI

    private val _stationInfo = MutableStateFlow(
        StationInfo(
            ids = listOf("71379"),
            names = listOf("PORTE MAILLOT"),
            linesList = listOf("1", "4", "7", "11", "14"),
            currentTraffics = listOf(1250),
            trends = listOf(12),
            peakTraffics = listOf(2100),
            peakTimes = listOf("18h30")
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

    // Chargement des prédictions avec gestion de la date
    fun loadPredictions(stationId: String, date: Date? = null, forceRefresh: Boolean = false) {
        val dateFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val dateString = date?.let { dateFormatter.format(it) } ?: dateFormatter.format(Date())

        viewModelScope.launch {
            try {
                _uiState.value = UiState.Loading

                val cacheKey = "$stationId-$dateString"

                // Vérification du cache
                val cachedPredictions = predictionsCache[cacheKey]
                if (!forceRefresh && cachedPredictions != null) {
                    _hourlyPredictions.value = cachedPredictions
                    _uiState.value = UiState.Success(false)
                    return@launch
                }

                // Récupération des prédictions
                val predictions = repository.getPredictions(stationId, dateString)

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
                Log.e("PredictionVM", "Erreur loadPredictions", e)
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

                // Utiliser loadPredictions avec null pour obtenir la date actuelle
                loadPredictions(stationId, null, forceRefresh)

            } catch (e: Exception) {
                _uiState.value = UiState.Error(e.message ?: "Erreur inconnue")
                _toastMessage.value = "Erreur: ${e.message}"
                Log.e("PredictionVM", "Erreur loadStationInfo", e)
            }
        }
    }

    // Rafraîchissement des données
    fun refresh() {
        val currentStationId = _stationInfo.value.id
        loadStationInfo(currentStationId, forceRefresh = true)
    }
}