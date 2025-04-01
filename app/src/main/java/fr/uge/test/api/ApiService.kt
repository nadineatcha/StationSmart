package fr.uge.test.api

import fr.uge.test.model.HourlyPrediction
import fr.uge.test.model.StationInfo
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
// Ajoutez ceci en haut du fichier
import fr.uge.test.model.Station
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor


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
}

object ApiClient {
    private const val BASE_URL = "http://10.0.2.2:44571/"

    // Ajouter le logging
    private val httpClient = OkHttpClient.Builder()
        .addInterceptor(HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        })
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(httpClient) // Ajouter cette ligne
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val api: FlowSightApi = retrofit.create(FlowSightApi::class.java)
}
