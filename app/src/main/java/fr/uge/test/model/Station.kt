package fr.uge.test.model

import com.google.gson.annotations.SerializedName
import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializer
import com.google.gson.reflect.TypeToken

data class Station(
    @SerializedName("id") val ids: List<String> = emptyList(),
    @SerializedName("name") val names: List<String> = emptyList(),
    @SerializedName("lines") val linesList: List<String> = emptyList(),
    @SerializedName("latitude") val latitudes: List<Double> = emptyList(),
    @SerializedName("longitude") val longitudes: List<Double> = emptyList(),
    @SerializedName("distance") val distances: List<Double>? = null,
    @SerializedName("current_traffic") val currentTraffics: List<Int>? = null
) {
    // Propriétés de commodité avec valeurs par défaut
    val id: String get() = ids.firstOrNull() ?: "inconnu"
    val name: String get() = names.firstOrNull() ?: "Nom inconnu"
    val lines: String get() = linesList.firstOrNull() ?: ""
    val latitude: Double get() = latitudes.firstOrNull() ?: 0.0
    val longitude: Double get() = longitudes.firstOrNull() ?: 0.0
    val distance: Double? get() = distances?.firstOrNull()
    val currentTraffic: Int get() = currentTraffics?.firstOrNull() ?: 0

    companion object {
        // Méthode pour créer un désérialiseur personnalisé si nécessaire
        fun createGsonBuilder(): GsonBuilder {
            return GsonBuilder().apply {
                registerTypeAdapter(
                    object : TypeToken<List<Station>>() {}.type,
                    JsonDeserializer { json, _, _ ->
                        json.asJsonArray.map { jsonElement ->
                            val obj = jsonElement.asJsonObject
                            Station(
                                ids = getStringList(obj, "id"),
                                names = getStringList(obj, "name"),
                                linesList = getStringList(obj, "lines"),
                                latitudes = getDoubleList(obj, "latitude"),
                                longitudes = getDoubleList(obj, "longitude"),
                                distances = getDoubleListOrNull(obj, "distance"),
                                currentTraffics = getIntListOrNull(obj, "current_traffic")
                            )
                        }
                    }
                )
            }
        }

        // Méthodes utilitaires pour extraire les listes
        private fun getStringList(obj: com.google.gson.JsonObject, key: String): List<String> {
            return if (obj.has(key) && !obj.get(key).isJsonNull) {
                when {
                    obj.get(key).isJsonArray -> obj.getAsJsonArray(key).map { it.asString }
                    obj.get(key).isJsonPrimitive -> listOf(obj.get(key).asString)
                    else -> emptyList()
                }
            } else emptyList()
        }

        private fun getDoubleList(obj: com.google.gson.JsonObject, key: String): List<Double> {
            return if (obj.has(key) && !obj.get(key).isJsonNull) {
                when {
                    obj.get(key).isJsonArray -> obj.getAsJsonArray(key).map { it.asDouble }
                    obj.get(key).isJsonPrimitive -> listOf(obj.get(key).asDouble)
                    else -> emptyList()
                }
            } else emptyList()
        }

        private fun getDoubleListOrNull(obj: com.google.gson.JsonObject, key: String): List<Double>? {
            return if (obj.has(key) && !obj.get(key).isJsonNull) {
                when {
                    obj.get(key).isJsonArray -> obj.getAsJsonArray(key).map { it.asDouble }
                    obj.get(key).isJsonPrimitive -> listOf(obj.get(key).asDouble)
                    else -> null
                }
            } else null
        }

        private fun getIntListOrNull(obj: com.google.gson.JsonObject, key: String): List<Int>? {
            return if (obj.has(key) && !obj.get(key).isJsonNull) {
                when {
                    obj.get(key).isJsonArray -> obj.getAsJsonArray(key).map { it.asInt }
                    obj.get(key).isJsonPrimitive -> listOf(obj.get(key).asInt)
                    else -> null
                }
            } else null
        }
    }
}