package fr.uge.test

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.platform.LocalContext
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import androidx.preference.PreferenceManager // Ajout de l'import nécessaire

@Composable
fun OSMMapView() {
    val context = LocalContext.current
    // Charger les stations une seule fois pour éviter de les recharger à chaque recomposition
    val stations = remember { loadStations(context) }

    // Utilisation d'AndroidView pour intégrer MapView dans Jetpack Compose
    AndroidView(
        factory = { context ->
            MapView(context).apply {
                // Charger les paramètres de configuration pour osmdroid
                Configuration.getInstance().load(context, PreferenceManager.getDefaultSharedPreferences(context))

                // Définir la source des tuiles de la carte
                setTileSource(TileSourceFactory.MAPNIK)

                // Initialiser les contrôles de zoom
                setMultiTouchControls(true)

                // Ajouter les stations sur la carte
                val mapOverlays = overlays
                stations.forEach { station ->
                    val marker = Marker(this)
                    marker.position = GeoPoint(station.latitude, station.longitude)
                    marker.title = "${station.name} - ${station.trafic} passagers"
                    marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                    mapOverlays.add(marker)
                }
            }
        }
    )
}

fun loadStations(context: Context): List<Station> {
    // Charger et parser le fichier JSON des stations
    return try {
        val json = context.assets.open("stations.json").bufferedReader().use { it.readText() }
        val gson = Gson()
        gson.fromJson(json, object : TypeToken<List<Station>>() {}.type)
    } catch (e: Exception) {
        e.printStackTrace() // Afficher l'erreur dans les logs
        emptyList() // Retourner une liste vide en cas d'erreur
    }
}

data class Station(
    val name: String,
    val latitude: Double,
    val longitude: Double,
    val trafic: Int
)
