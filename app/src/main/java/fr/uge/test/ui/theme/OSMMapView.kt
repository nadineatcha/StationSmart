package fr.uge.test.ui.theme

import android.content.Context
import android.graphics.drawable.Drawable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import fr.uge.test.R
import fr.uge.test.model.Station
import fr.uge.test.model.TrafficLevel
import fr.uge.test.viewmodel.StationViewModel
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import androidx.preference.PreferenceManager

@Composable
fun OSMMapView(
    modifier: Modifier = Modifier,
    viewModel: StationViewModel = viewModel(),
    onMarkerClick: (Station) -> Unit
) {
    val context = LocalContext.current
    val stations by viewModel.searchResults.collectAsState()
    val selectedStation by viewModel.selectedStation.collectAsState()

    remember(context) {
        Configuration.getInstance().load(context, PreferenceManager.getDefaultSharedPreferences(context))
        true // Correction du remember
    }

    AndroidView(
        modifier = modifier.fillMaxSize(),
        factory = { context ->
            MapView(context).apply {
                setTileSource(TileSourceFactory.MAPNIK)
                setMultiTouchControls(true)
                controller.setZoom(12.0)
                controller.setCenter(GeoPoint(48.8566, 2.3522)) // Paris par défaut
            }
        },
        update = { mapView ->
            mapView.overlays.clear()

            stations.forEach { station ->
                Marker(mapView).apply {
                    position = GeoPoint(station.latitude, station.longitude)
                    title = station.name
                    snippet = "Lignes: ${station.lines}"

                    // Utilisation du marqueur par défaut avec couleur dynamique
                    val color = when(viewModel.getTrafficLevel(station)) {
                        TrafficLevel.LOW -> Color.Green
                        TrafficLevel.MEDIUM -> Color.Yellow
                        TrafficLevel.HIGH -> Color.Red
                    }

                    val baseDrawable = ContextCompat.getDrawable(
                        context,
                        org.osmdroid.library.R.drawable.marker_default
                    )?.mutate()

                    baseDrawable?.let {
                        DrawableCompat.setTint(it, color.toArgb())
                        icon = it
                    }

                    setOnMarkerClickListener { _, _ ->
                        onMarkerClick(station)
                        true
                    }
                }.also { marker ->
                    mapView.overlays.add(marker)
                }
            }

            selectedStation?.let { station ->
                mapView.controller.animateTo(GeoPoint(station.latitude, station.longitude))
                mapView.controller.setZoom(15.0)
            }

            mapView.invalidate()
        }
    )
}