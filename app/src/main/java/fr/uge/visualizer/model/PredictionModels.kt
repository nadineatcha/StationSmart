package fr.uge.visualizer.model

data class StationInfo(
    val id: String = "",
    val name: String = "",
    val lines: String = "",
    val currentTraffic: Int = 0,
    val trend: Int = 0,
    val peakTraffic: Int = 0,
    val peakTime: String = ""
)

data class HourlyPrediction(
    val time: String,
    val count: Int,
    val percentage: Int,
    val trend: String // "up" ou "down"
)