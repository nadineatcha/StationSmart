package fr.uge.test.repository

import fr.uge.test.api.ApiClient
import fr.uge.test.model.HourlyPrediction
import fr.uge.test.model.StationInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import fr.uge.test.model.Station

class StationRepository {
    private val api = ApiClient.api

    // Charger toutes les stations
    suspend fun getAllStations(): List<Station> = withContext(Dispatchers.IO) {
        try {
            api.getAllStations()
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    // Rechercher des stations par nom
    suspend fun searchStations(query: String): List<Station> = withContext(Dispatchers.IO) {
        try {
            api.searchStations(query)
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    // Obtenir les stations à proximité
    suspend fun getNearbyStations(latitude: Double, longitude: Double): List<Station> = withContext(Dispatchers.IO) {
        try {
            api.getNearbyStations(latitude, longitude)
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    // Obtenir les informations d'une station
    suspend fun getStationInfo(stationId: String): StationInfo? = withContext(Dispatchers.IO) {
        try {
            api.getStationInfo(stationId)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    // Obtenir les prédictions pour une station
    suspend fun getPredictions(stationId: String, date: Date? = null): List<HourlyPrediction> = withContext(Dispatchers.IO) {
        try {
            val dateString = date?.let {
                SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(it)
            }
            api.getPredictions(stationId, dateString)
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }
}
