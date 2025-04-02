package fr.uge.test.repository

import android.util.Log
import fr.uge.test.api.ApiClient
import fr.uge.test.api.ApiClient.api
import fr.uge.test.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.text.SimpleDateFormat
import java.util.*

class StationRepository {
    private val api = ApiClient.api

    private suspend fun <T> retryOnError(
        block: suspend () -> T,
        fallback: T,
        operationName: String
    ): T = withContext(Dispatchers.IO) {
        try {
            retryOnServerError(block)
        } catch (e: Exception) {
            Log.e("API_ERROR", "$operationName failed: ${e.message}", e)
            fallback
        }
    }

    private suspend fun <T> retryOnServerError(
        block: suspend () -> T,
        maxRetries: Int = 3,
        initialDelay: Long = 1000,
        maxDelay: Long = 10000,
        factor: Double = 2.0
    ): T {
        var currentDelay = initialDelay
        repeat(maxRetries) { attempt ->
            try {
                return block()
            } catch (e: Exception) {
                when {
                    (e is HttpException && e.code() < 500) -> throw e
                    attempt == maxRetries - 1 -> throw e
                    else -> {
                        Log.w("API_RETRY", "Retry $attempt/$maxRetries")
                        delay(currentDelay)
                        currentDelay = (currentDelay * factor).toLong().coerceAtMost(maxDelay)
                    }
                }
            }
        }
        throw IllegalStateException("Unreachable code")
    }

    suspend fun getAllStations(): List<Station> = retryOnError(
        block = { api.getAllStations() },
        fallback = emptyList(),
        operationName = "Get all stations"
    )

    suspend fun searchStations(query: String): List<Station> = retryOnError(
        block = { api.searchStations(query) },
        fallback = emptyList(),
        operationName = "Search stations"
    )

    suspend fun getNearbyStations(latitude: Double, longitude: Double): List<Station> = retryOnError(
        block = { api.getNearbyStations(latitude, longitude) },
        fallback = listOf(
            Station(
                ids = listOf("1"),
                names = listOf("Châtelet"),
                linesList = listOf("1", "4", "7", "11", "14"),
                latitudes = listOf(48.8586),
                longitudes = listOf(2.3491)
            )
        ),
        operationName = "Get nearby stations"
    )

    suspend fun getStationInfo(stationId: String): StationInfo = retryOnError(
        block = { api.getStationInfo(stationId) },
        fallback = StationInfo.default(stationId),
        operationName = "Get station info"
    )

    /*suspend fun getPredictions(stationId: String, date: String = null): List<HourlyPrediction> {
        val dateString = date?.let {
            SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(it)
        }*/
    suspend fun getPredictions(stationId: String, date: String? = null): List<HourlyPrediction> {
        val dateString: String? = date

        return retryOnError(
            block = { api.getPredictions(stationId, dateString) },
            fallback = listOf(
                HourlyPrediction("09:00", 500, 30, "up"),
                HourlyPrediction("12:00", 1200, 65, "up")
            ),
            operationName = "Get predictions"
        )
    }


    suspend fun getNotifications(): List<Notification> = retryOnError(
        block = { api.getNotifications().map { it.toNotification() } },
        fallback = listOf(
            Notification(
                title = "Maintenance programmée",
                message = "Station République fermée pour maintenance",
                time = "Il y a 1 heure",
                type = "info"
            )
        ),
        operationName = "Get notifications"
    )
}
