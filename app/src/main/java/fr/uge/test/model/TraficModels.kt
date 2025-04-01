package fr.uge.test.model

import com.google.gson.annotations.SerializedName



// Modèle pour les informations de trafic d'une station
/*data class StationInfo(
    val id: String,
    val name: String,
    val lines: String,
    @SerializedName("currentTraffic") val currentTraffic: Int,
    val trend: Int,
    @SerializedName("peakTraffic") val peakTraffic: Int,
    @SerializedName("peakTime") val peakTime: String
)*/
data class StationInfo(
    @SerializedName("id") val ids: List<String>,
    @SerializedName("name") val names: List<String>,
    @SerializedName("currentTraffic") val currentTraffics: List<Int>,
    @SerializedName("trend") val trends: List<Int>,
    @SerializedName("peakTraffic") val peakTraffics: List<Int>,
    @SerializedName("peakTime") val peakTimes: List<String>
) {
    val id: String get() = ids.first()
    val name: String get() = names.first()
    val currentTraffic: Int get() = currentTraffics.first()
    val trend: Int get() = trends.first()
    val peakTraffic: Int get() = peakTraffics.first()
    val peakTime: String get() = peakTimes.first()
}

// Modèle pour les prédictions horaires
data class HourlyPrediction(
    val time: String,
    val count: Int,
    @SerializedName("percentageCapacity") val percentageCapacity: Int,
    val trend: String  // "up" ou "down"
)

// Enum pour les niveaux de trafic
enum class TrafficLevel {
    LOW, MEDIUM, HIGH;

    companion object {
        fun fromTraffic(traffic: Int): TrafficLevel {
            return when {
                traffic < 800 -> LOW
                traffic < 1500 -> MEDIUM
                else -> HIGH
            }
        }
    }
}
