package fr.uge.visualizer.model

// Modèle information station avec valeurs par défaut pour éviter les problèmes de null
data class StationInfo(
    val id: String = "",
    val name: String = "",
    val lines: String = "",
    val currentTraffic: Int = 0,
    val trend: Int = 0,
    val peakTraffic: Int = 0,
    val peakTime: String = ""
)

// Modèle de prédiction horaire avec correction du nom de la propriété
// "percentageCapacity" au lieu de "percentage" pour correspondre à l'API
data class HourlyPrediction(
    val time: String = "",
    val count: Int = 0,
    val percentageCapacity: Int = 0, // Renommé pour correspondre à la propriété dans l'API
    val trend: String = "" // "up" ou "down"
)

data class Notification(
    val id: List<String>,
    val title: List<String>,
    val message: List<String>,
    val time: List<String>,
    val type: List<String>,
    val group: List<String>,
    val category: List<String>
)
data class Station(
    val id: String = "",
    val name: String = "",
    val lines: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val distance: Float? = null
)
