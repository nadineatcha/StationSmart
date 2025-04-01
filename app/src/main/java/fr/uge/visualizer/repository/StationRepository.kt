package fr.uge.visualizer.repository

import android.util.Log
import fr.uge.visualizer.model.HourlyPrediction
import fr.uge.visualizer.model.Notification
import fr.uge.visualizer.model.Station
import fr.uge.visualizer.model.StationInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import java.io.IOException
import java.util.concurrent.TimeUnit

// Interface pour les endpoints API
interface StationApi {
    @GET("predictions")
    suspend fun getPredictions(
        @Query("station_id") stationId: String,
        @Query("date") date: String
    ): List<HourlyPrediction>

    @GET("station_info")
    suspend fun getStationInfo(
        @Query("station_id") stationId: String
    ): StationInfo

    @GET("nearby_stations")
    suspend fun getNearbyStations(
        @Query("lat") latitude: Double,
        @Query("lon") longitude: Double
    ): List<Station>

    @GET("notifications")
    suspend fun getNotifications(): List<Notification>
}

class StationRepository {
    private val api: StationApi

    init {
        // Création d'un intercepteur pour les logs
        val loggingInterceptor = HttpLoggingInterceptor { message ->
            Log.d("API_NETWORK", message)
        }.apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        // Intercepteur pour les détails de la requête et réponse
        val requestResponseInterceptor = { chain: okhttp3.Interceptor.Chain ->
            val request = chain.request()
            Log.d("API_REQUEST", "URL: ${request.url}")
            Log.d("API_REQUEST", "Headers: ${request.headers}")
            try {
                val response = chain.proceed(request)
                Log.d("API_RESPONSE", "Code: ${response.code}")
                if (!response.isSuccessful) {
                    Log.e("API_RESPONSE", "Erreur ${response.code}: ${response.message}")
                    try {
                        Log.e("API_RESPONSE", "Body: ${response.peekBody(Long.MAX_VALUE).string()}")
                    } catch (e: Exception) {
                        Log.e("API_RESPONSE", "Impossible de lire le corps de la réponse: ${e.message}")
                    }
                }
                response
            } catch (e: Exception) {
                Log.e("API_ERROR", "Exception pendant l'appel: ${e.message}")
                throw e
            }
        }

        // Client OkHttp avec les intercepteurs et des timeouts plus longs
        val client = OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .addInterceptor(requestResponseInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .retryOnConnectionFailure(true)
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl("http://10.0.2.2:32580/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        api = retrofit.create(StationApi::class.java)
    }

    // Fonction utilitaire pour retenter les appels en cas d'erreur serveur
    private suspend fun <T> retryOnServerError(
        times: Int = 3,
        initialDelay: Long = 500,
        maxDelay: Long = 5000,
        factor: Double = 2.0,
        block: suspend () -> T
    ): T {
        var currentDelay = initialDelay
        var lastException: Exception? = null

        repeat(times) { attempt ->
            try {
                return block()
            } catch (e: HttpException) {
                lastException = e
                // Réessayer uniquement pour les erreurs serveur (500+)
                if (e.code() >= 500) {
                    Log.w("API_RETRY", "Tentative ${attempt+1}/$times a échoué avec code ${e.code()}, nouvelle tentative après ${currentDelay}ms")
                    delay(currentDelay)
                    currentDelay = (currentDelay * factor).toLong().coerceAtMost(maxDelay)
                } else {
                    throw e // Pour les autres erreurs HTTP, propager immédiatement
                }
            } catch (e: IOException) {
                // Réessayer pour les erreurs réseau
                lastException = e
                Log.w("API_RETRY", "Tentative ${attempt+1}/$times a échoué avec une erreur réseau: ${e.message}, nouvelle tentative après ${currentDelay}ms")
                delay(currentDelay)
                currentDelay = (currentDelay * factor).toLong().coerceAtMost(maxDelay)
            } catch (e: Exception) {
                lastException = e
                // Pour les autres types d'erreurs, propager immédiatement
                throw e
            }
        }

        throw lastException ?: IllegalStateException("Toutes les tentatives ont échoué")
    }

    // Méthode pour obtenir les notifications
    suspend fun getNotifications(): List<Notification> {
        return withContext(Dispatchers.IO) {
            try {
                Log.d("API_CALL", "Tentative d'appel à getNotifications")
                val result = retryOnServerError {
                    api.getNotifications()
                }
                Log.d("API_CALL", "Succès: ${result.size} notifications reçues")
                result
            } catch (e: Exception) {
                Log.e("API_CALL", "Erreur lors de l'appel API: ${e.message}", e)
                // En cas d'erreur, utiliser des données de secours
                listOf(
                    Notification(
                        id = listOf("1"),
                        title = listOf("Trafic dense à Châtelet"),
                        message = listOf("Affluence importante prévue entre 17h et 19h."),
                        time = listOf("Il y a 5 minutes"),
                        type = listOf("urgent"),
                        group = listOf("Aujourd'hui"),
                        category = listOf("traffic")
                    ),
                    Notification(
                        id = listOf("2"),
                        title = listOf("Mise à jour des prévisions"),
                        message = listOf("Nouvelles données disponibles pour votre trajet habituel."),
                        time = listOf("Il y a 2 heures"),
                        type = listOf("info"),
                        group = listOf("Aujourd'hui"),
                        category = listOf("info")
                    ),
                    Notification(
                        id = listOf("3"),
                        title = listOf("Trafic fluide"),
                        message = listOf("Le trafic est redevenu normal sur votre ligne."),
                        time = listOf("Hier à 18:30"),
                        type = listOf("success"),
                        group = listOf("Hier"),
                        category = listOf("traffic")
                    )
                )
            }
        }
    }

    // Méthode pour obtenir les prédictions
    suspend fun getPredictions(stationId: String, date: String): List<HourlyPrediction> {
        return withContext(Dispatchers.IO) {
            try {
                Log.d("API_CALL", "Tentative de récupération des prédictions pour la station $stationId")
                val result = retryOnServerError {
                    api.getPredictions(stationId, date)
                }
                Log.d("API_CALL", "Succès: ${result.size} prédictions reçues")
                result
            } catch (e: Exception) {
                Log.e("API_CALL", "Erreur lors de la récupération des prédictions", e)
                // Données de secours
                listOf(
                    HourlyPrediction("09:00", 500, 30, "up"),
                    HourlyPrediction("12:00", 1200, 65, "up"),
                    HourlyPrediction("15:00", 800, 45, "down"),
                    HourlyPrediction("18:00", 2100, 90, "up")
                )
            }
        }
    }

    // Méthode pour obtenir les informations de station
    suspend fun getStationInfo(stationId: String): StationInfo {
        return withContext(Dispatchers.IO) {
            try {
                Log.d("API_CALL", "Tentative de récupération des informations pour la station $stationId")
                val result = retryOnServerError {
                    api.getStationInfo(stationId)
                }
                Log.d("API_CALL", "Succès: informations de station reçues")
                result
            } catch (e: Exception) {
                Log.e("API_CALL", "Erreur lors de la récupération des informations de station", e)
                // Données de secours
                StationInfo(
                    id = stationId,
                    name = "Station $stationId",
                    lines = "1, 4, 7",
                    currentTraffic = 1000,
                    trend = 5,
                    peakTraffic = 2000,
                    peakTime = "18h00"
                )
            }
        }
    }

    // Méthode pour obtenir les stations à proximité
    suspend fun getNearbyStations(latitude: Double, longitude: Double): List<Station> {
        return withContext(Dispatchers.IO) {
            try {
                Log.d("API_CALL", "Tentative de récupération des stations à proximité de lat=$latitude, lon=$longitude")
                val result = retryOnServerError {
                    api.getNearbyStations(latitude, longitude)
                }
                Log.d("API_CALL", "Succès: ${result.size} stations reçues")
                result
            } catch (e: Exception) {
                Log.e("API_CALL", "Erreur lors de la récupération des stations à proximité", e)
                // Données de secours
                listOf(
                    Station(
                        id = "1",
                        name = "Châtelet",
                        lines = "1, 4, 7, 11, 14",
                        latitude = 48.8586,
                        longitude = 2.3491
                    ),
                    Station(
                        id = "2",
                        name = "Les Halles",
                        lines = "4, A, B",
                        latitude = 48.8637,
                        longitude = 2.3453
                    )
                )
            }
        }
    }
}