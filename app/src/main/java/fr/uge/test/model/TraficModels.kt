package fr.uge.test.model

import com.google.gson.annotations.SerializedName

/*data class StationInfo(
    @SerializedName("id") val ids: List<String>,
    @SerializedName("name") val names: List<String>,
    @SerializedName("currentTraffic") val currentTraffics: List<Int>,
    //@SerializedName("lines") val lines: List<String>,
    @SerializedName("trend") val trends: List<Int>,
    @SerializedName("peakTraffic") val peakTraffics: List<Int>,
    @SerializedName("peakTime") val peakTimes: List<String>
) {
    val id: String get() = ids.first()
    val name: String get() = names.first()
    val currentTraffic: Int get() = currentTraffics.first()
    //val lines: String get() = lines.first()
    val trend: Int get() = trends.first()
    val peakTraffic: Int get() = peakTraffics.first()
    val peakTime: String get() = peakTimes.first()
}*/


data class StationInfo(
    @SerializedName("id") val ids: List<String> = emptyList(),
    @SerializedName("name") val names: List<String> = emptyList(),
    @SerializedName("currentTraffic") val currentTraffics: List<Int> = emptyList(),
    @SerializedName("lines") val linesList: List<String> = emptyList(),
    @SerializedName("trend") val trends: List<Int> = emptyList(),
    @SerializedName("peakTraffic") val peakTraffics: List<Int> = emptyList(),
    @SerializedName("peakTime") val peakTimes: List<String> = emptyList()
) {
    // Correction 3: Getters sécurisés
    val id: String get() = ids.firstOrNull() ?: "inconnu"
    val name: String get() = names.firstOrNull() ?: "Station inconnue"
    val currentTraffic: Int get() = currentTraffics.firstOrNull() ?: 0
    val lines: String get() = linesList.joinToString(", ")
    val trend: Int get() = trends.firstOrNull() ?: 0
    val peakTraffic: Int get() = peakTraffics.firstOrNull() ?: 0
    val peakTime: String get() = peakTimes.firstOrNull() ?: "--:--"

    companion object {
        // Correction 4: Factory method amélioré
        fun default(stationId: String) = StationInfo(
            ids = listOf(stationId),
            names = listOf("Station $stationId"),
            linesList = listOf("Ligne par défaut"),
            currentTraffics = listOf(0),
            trends = listOf(0),
            peakTraffics = listOf(0),
            peakTimes = listOf("N/A")
        )
    }
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

data class Notification(
    val id: String = "",
    val title: String = "",
    val message: String = "",
    val time: String = "",
    val type: String = "", // "urgent", "info", "success"
    val group: String = "", // "Aujourd'hui", "Hier", "Cette semaine"
    val category: String = "" // "traffic", "stations", "info"
)
