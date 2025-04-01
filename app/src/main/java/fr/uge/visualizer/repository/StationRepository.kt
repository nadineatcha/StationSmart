package fr.uge.visualizer.repository

import android.util.Log
import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializer
import com.google.gson.reflect.TypeToken
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

// Classe d'adaptation pour les notifications de l'API
data class ApiNotification(
    val id: List<String> = listOf(),
    val title: List<String> = listOf(),
    val message: List<String> = listOf(),
    val time: List<String> = listOf(),
    val type: List<String> = listOf(),
    val group: List<String> = listOf(),
    val category: List<String> = listOf()
)

// Extension pour convertir ApiNotification en Notification
fun ApiNotification.toNotification(): Notification {
    return Notification(
        id = id.firstOrNull() ?: "",
        title = title.firstOrNull() ?: "",
        message = message.firstOrNull() ?: "",
        time = time.firstOrNull() ?: "",
        type = type.firstOrNull() ?: "",
        group = group.firstOrNull() ?: "",
        category = category.firstOrNull() ?: ""
    )
}

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
    suspend fun getNotifications(): List<ApiNotification>
}

class StationRepository {
    private val api: StationApi
    private val client = OkHttpClient.Builder()
        .addInterceptor(HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        })
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    // Dans la classe StationRepository, modifiez le bloc init
    init {
        // Reste du code...

        // Créer un Gson personnalisé avec un adaptateur pour les tableaux
        val gson = GsonBuilder()
            .registerTypeAdapter(
                object : TypeToken<List<ApiNotification>>() {}.type,
                JsonDeserializer<List<ApiNotification>> { json, typeOfT, context ->
                    val notifications = ArrayList<ApiNotification>()
                    val jsonArray = json.asJsonArray

                    for (i in 0 until jsonArray.size()) {
                        val jsonObject = jsonArray.get(i).asJsonObject

                        val id = jsonObject.getAsJsonArray("id").map { it.asString }
                        val title = jsonObject.getAsJsonArray("title").map { it.asString }
                        val message = jsonObject.getAsJsonArray("message").map { it.asString }
                        val time = jsonObject.getAsJsonArray("time").map { it.asString }
                        val type = jsonObject.getAsJsonArray("type").map { it.asString }
                        val group = jsonObject.getAsJsonArray("group").map { it.asString }
                        val category = jsonObject.getAsJsonArray("category").map { it.asString }

                        notifications.add(
                            ApiNotification(
                                id = id,
                                title = title,
                                message = message,
                                time = time,
                                type = type,
                                group = group,
                                category = category
                            )
                        )
                    }

                    notifications
                }
            )
            .create()

        // Utiliser ce Gson pour Retrofit
        val retrofit = Retrofit.Builder()
            .baseUrl("http://10.0.2.2:32580/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create(gson))
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
                val apiNotifications = retryOnServerError {
                    api.getNotifications()
                }

                // Convertir les ApiNotification en Notification
                val notifications = apiNotifications.map { it.toNotification() }

                Log.d("API_CALL", "Succès: ${notifications.size} notifications reçues")
                notifications
            } catch (e: Exception) {
                Log.e("API_CALL", "Erreur lors de l'appel API: ${e.message}", e)
                // En cas d'erreur, utiliser des données de secours
                listOf(
                    Notification(
                        id = "1",
                        title = "Trafic dense à Châtelet",
                        message = "Affluence importante prévue entre 17h et 19h.",
                        time = "Il y a 5 minutes",
                        type = "urgent",
                        group = "Aujourd'hui",
                        category = "traffic"
                    ),
                    Notification(
                        id = "2",
                        title = "Mise à jour des prévisions",
                        message = "Nouvelles données disponibles pour votre trajet habituel.",
                        time = "Il y a 2 heures",
                        type = "info",
                        group = "Aujourd'hui",
                        category = "info"
                    ),
                    Notification(
                        id = "3",
                        title = "Trafic fluide",
                        message = "Le trafic est redevenu normal sur votre ligne.",
                        time = "Hier à 18:30",
                        type = "success",
                        group = "Hier",
                        category = "traffic"
                    ),
                    Notification(
                        id = "4",
                        title = "Station Concorde fermée",
                        message = "Station fermée pour travaux jusqu'à 18h.",
                        time = "Hier à 10:15",
                        type = "urgent",
                        group = "Hier",
                        category = "stations"
                    ),
                    Notification(
                        id = "5",
                        title = "Perturbation ligne 1",
                        message = "Ralentissement du trafic entre Nation et Châtelet.",
                        time = "Il y a 30 minutes",
                        type = "urgent",
                        group = "Aujourd'hui",
                        category = "traffic"
                    ),
                    Notification(
                        id = "6",
                        title = "Maintenance programmée",
                        message = "Station République fermée pour maintenance.",
                        time = "Il y a 1 heure",
                        type = "info",
                        group = "Aujourd'hui",
                        category = "stations"
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