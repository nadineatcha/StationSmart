package fr.uge.test.model

import com.google.gson.annotations.SerializedName

data class Station(
    @SerializedName("id") val ids: List<String> = emptyList(),
    @SerializedName("name") val names: List<String> = emptyList(),
    @SerializedName("lines") val linesList: List<String> = emptyList(),
    @SerializedName("latitude") val latitudes: List<Double> = emptyList(),
    @SerializedName("longitude") val longitudes: List<Double> = emptyList(),
    @SerializedName("current_traffic") val currentTraffics: List<Int> = emptyList()
) {
    // Utiliser firstOrNull() avec valeurs par défaut
    val id: String get() = ids.firstOrNull() ?: "inconnu"
    val name: String get() = names.firstOrNull() ?: "Nom inconnu"
    val lines: String get() = linesList.firstOrNull() ?: ""
    val latitude: Double get() = latitudes.firstOrNull() ?: 0.0
    val longitude: Double get() = longitudes.firstOrNull() ?: 0.0
    val currentTraffic: Int get() = currentTraffics.firstOrNull() ?: 0
}