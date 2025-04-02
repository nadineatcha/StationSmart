package fr.uge.test.api

import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializer
import com.google.gson.reflect.TypeToken
import fr.uge.test.model.*
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import java.util.concurrent.TimeUnit

interface FlowSightApi {
    @GET("all_stations")
    suspend fun getAllStations(): List<Station>

    @GET("search_stations")
    suspend fun searchStations(@Query("query") query: String): List<Station>

    @GET("nearby_stations")
    suspend fun getNearbyStations(
        @Query("lat") latitude: Double,
        @Query("lon") longitude: Double,
        @Query("limit") limit: Int = 10
    ): List<Station>

    @GET("station_info")
    suspend fun getStationInfo(@Query("station_id") stationId: String): StationInfo

    @GET("predictions")
    suspend fun getPredictions(
        @Query("station_id") stationId: String,
        @Query("date") date: String? = null
    ): List<HourlyPrediction>

    @GET("notifications")
    suspend fun getNotifications(): List<ApiNotification>
}

// Modèle de données pour les notifications de l'API
data class ApiNotification(
    val id: List<String> = listOf(),
    val title: List<String> = listOf(),
    val message: List<String> = listOf(),
    val time: List<String> = listOf(),
    val type: List<String> = listOf(),
    val group: List<String> = listOf(),
    val category: List<String> = listOf()
) {
    fun toNotification() = Notification(
        id = id.firstOrNull() ?: "",
        title = title.firstOrNull() ?: "",
        message = message.firstOrNull() ?: "",
        time = time.firstOrNull() ?: "",
        type = type.firstOrNull() ?: "info",
        group = group.firstOrNull() ?: "Général",
        category = category.firstOrNull() ?: "info"
    )
}

object ApiClient {
    private const val BASE_URL = "http://10.0.2.2:22786/"

    private val gson = Station.createGsonBuilder()
        .apply {
            // Autres configurations personnalisées si nécessaire
        }
        .create()

    private val httpClient = OkHttpClient.Builder()
        .addInterceptor(HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        })
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(httpClient)
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build()

    val api: FlowSightApi = retrofit.create(FlowSightApi::class.java)
}