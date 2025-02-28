package fr.uge.visualizer.viewmodel

import androidx.lifecycle.ViewModel
import fr.uge.visualizer.model.HourlyPrediction
import fr.uge.visualizer.model.StationInfo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class PredictionViewModel : ViewModel() {

    private val _stationInfo = MutableStateFlow(
        StationInfo(
            id = "12345",
            name = "Châtelet",
            lines = "1, 4, 7, 11, 14",
            currentTraffic = 1250,
            trend = 12,
            peakTraffic = 2100,
            peakTime = "18h30"
        )
    )
    val stationInfo: StateFlow<StationInfo> = _stationInfo

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
}

