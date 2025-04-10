package fr.uge.visualizer
import android.R
import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.zIndex
import androidx.core.content.ContextCompat
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import com.google.gson.reflect.TypeToken
import fr.uge.visualizer.ui.theme.MonDetailsTheme
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import java.io.InputStreamReader

// Définition des types de lignes
enum class LineType {
    RER,
    METRO
}


// Classes de données pour le JSON
data class JsonLocation(
    val latitude: Double,
    val longitude: Double
)

data class JsonLine(
    val name: String,
    val type: String
)

data class JsonStation(
    val name: String,
    val location: JsonLocation,
    val address: String,
    val lines: List<JsonLine>,
    @SerializedName("dailyPassengers") val dailyPassengers: Int,
    @SerializedName("peakHours") val peakHours: String,
    @SerializedName("openingHours") val openingHours: String
) {
    fun toStation(): Station {
        return Station(
            name = name,
            location = GeoPoint(location.latitude, location.longitude),
            address = address,
            lines = lines.map { line ->
                Line(
                    name = line.name,
                    type = when (line.type) {
                        "RER" -> LineType.RER
                        "METRO" -> LineType.METRO
                        else -> LineType.METRO
                    }
                )
            },
            dailyPassengers = dailyPassengers,
            peakHours = peakHours,
            openingHours = openingHours
        )
    }
}

// Classes originales
data class Line(
    val name: String,
    val type: LineType
)

data class Station(
    val name: String,
    val location: GeoPoint,
    val address: String,
    val lines: List<Line>,
    val dailyPassengers: Int,
    val peakHours: String,
    val openingHours: String
)

fun getLineColor(line: String): Color {
    return when (line) {
        "1" -> Color(0xFFF3D03E)
        "2" -> Color(0xFF0065AE)
        "3" -> Color(0xFF9B642B)
        "4" -> Color(0xFFBE418D)
        "5" -> Color(0xFFFF7F27)
        "6" -> Color(0xFF6EC4B0)
        "7" -> Color(0xFFF59EB6)
        "8" -> Color(0xFFC7A8D9)
        "9" -> Color(0xFFD1C5A6)
        "10" -> Color(0xFFE3B32A)
        "11" -> Color(0xFF8E6538)
        "12" -> Color(0xFF007852)
        "13" -> Color(0xFF98D4E2)
        "14" -> Color(0xFF662483)
        "A" -> Color(0xFFE2231A)
        "B" -> Color(0xFF7BA3DC)
        "C" -> Color(0xFFF9A01B)
        "D" -> Color(0xFF00A74A)
        "E" -> Color(0xFFD48FCD)
        else -> Color.Gray
    }
}

// Activité principale
class interfacedetails : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Configuration.getInstance().load(this, getSharedPreferences("osm", MODE_PRIVATE))
        setContent {
            MonDetailsTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    StationFinderApp()
                }
            }
        }
    }
}

private fun loadStationsFromJson(context: Context): List<Station> {
    return try {
        val inputStream = context.assets.open("stations.json")
        val reader = InputStreamReader(inputStream)
        val type = object : TypeToken<List<JsonStation>>() {}.type
        Gson().fromJson<List<JsonStation>>(reader, type).map { it.toStation() }
    } catch (e: Exception) {
        e.printStackTrace()
        emptyList()
    }
}

@Composable
fun BottomNavigationBar() {
    var selectedItem by remember { mutableStateOf(0) }
    val items = listOf("Accueil", "Notifications", "Paramètres")
    val icons = listOf(
        Icons.Filled.Home,
        Icons.Filled.Notifications,
        Icons.Filled.Settings
    )

    NavigationBar {
        items.forEachIndexed { index, item ->
            NavigationBarItem(
                icon = { Icon(icons[index], contentDescription = item) },
                label = { Text(item) },
                selected = selectedItem == index,
                onClick = { selectedItem = index }
            )
        }
    }
}

@Composable
fun StationFinderApp() {
    val context = LocalContext.current
    val stations = remember { loadStationsFromJson(context) }

    var searchQuery by remember { mutableStateOf("") }
    var selectedStation by remember { mutableStateOf<Station?>(null) }
    var showInfo by remember { mutableStateOf(false) }
    var showSuggestions by remember { mutableStateOf(false) }

    val filteredStations = stations.filter { it.name.contains(searchQuery, ignoreCase = true) }

    Scaffold { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                OsmMapView(
                    stations = selectedStation?.let { listOf(it) } ?: emptyList(),
                    selectedStation = selectedStation,
                    onStationSelected = { station ->
                        selectedStation = station
                        showInfo = true
                        showSuggestions = false
                        searchQuery = station.name
                    },
                    modifier = Modifier.fillMaxSize()
                )

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .zIndex(1f)
                ) {
                    SearchBar(
                        query = searchQuery,
                        onQueryChange = {
                            searchQuery = it
                            showSuggestions = it.isNotEmpty()
                        },
                        onFocusChanged = { focused ->
                            showSuggestions = focused && searchQuery.isNotEmpty()
                        }
                    )

                    if (showSuggestions && filteredStations.isNotEmpty()) {
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(max = 200.dp),
                            color = MaterialTheme.colorScheme.surface,
                            shadowElevation = 6.dp,
                            tonalElevation = 6.dp,
                            shape = MaterialTheme.shapes.medium
                        ) {
                            LazyColumn {
                                items(filteredStations.size) { index ->
                                    StationSuggestionItem(
                                        station = filteredStations[index],
                                        onClick = {
                                            selectedStation = filteredStations[index]
                                            showInfo = true
                                            showSuggestions = false
                                            searchQuery = filteredStations[index].name
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }

            if (selectedStation != null) {
                Button(
                    onClick = { showInfo = !showInfo },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    shape = MaterialTheme.shapes.large,
                    elevation = ButtonDefaults.buttonElevation(8.dp)
                ) {
                    Text("Voir les détails de la station", color = MaterialTheme.colorScheme.onPrimary)
                }

                if (showInfo) {
                    InfoPanel(station = selectedStation!!)
                }
            }
        }
    }
}

@Composable
fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onFocusChanged: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    var isFocused by remember { mutableStateOf(false) }

    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
            .background(Color.White, shape = MaterialTheme.shapes.medium)
            .onFocusChanged {
                isFocused = it.isFocused
                onFocusChanged(it.isFocused)
            },
        placeholder = { Text("Rechercher une station", color = Color.Gray) },
        textStyle = LocalTextStyle.current.copy(color = Color.DarkGray),
        leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search", tint = Color.Gray) },
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White,
            focusedBorderColor = Color.Gray,
            unfocusedBorderColor = Color.Gray,
            focusedTextColor = Color.DarkGray,
            unfocusedTextColor = Color.DarkGray
        ),
        shape = MaterialTheme.shapes.medium
    )
}

@Composable
fun OsmMapView(
    stations: List<Station>,
    selectedStation: Station?,
    onStationSelected: (Station) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    AndroidView(
        modifier = modifier.fillMaxSize(),
        factory = { ctx ->
            MapView(ctx).apply {
                setTileSource(TileSourceFactory.MAPNIK)
                setMultiTouchControls(true)
                controller.setZoom(15.0)
                controller.setCenter(GeoPoint(48.8566, 2.3522))
            }
        },
        update = { mapView ->
            mapView.overlays.clear()
            stations.forEach { station ->
                Marker(mapView).apply {
                    position = station.location
                    title = station.name
                    setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                    icon = ContextCompat.getDrawable(context, R.drawable.ic_menu_mylocation)?.apply {
                        setTint(android.graphics.Color.RED)
                    }
                    setOnMarkerClickListener { _, _ ->
                        onStationSelected(station)
                        true
                    }
                }.also { mapView.overlays.add(it) }
            }

            val centerPoint = selectedStation?.location ?: GeoPoint(48.8566, 2.3522)
            mapView.controller.animateTo(centerPoint, 15.0, 1000L)
            mapView.invalidate()
        }
    )
}

@Composable
fun StationSuggestionItem(
    station: Station,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = station.name,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = "Lignes: ${station.lines.joinToString(", ") { it.name }}",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray
            )
        }
        Icon(Icons.Default.LocationOn, contentDescription = "Location", tint = Color(0xFF3F51B5))
    }
}

@Composable
fun InfoPanel(station: Station) {
    var selectedTab by remember { mutableStateOf(0) }

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF3F51B5))
                .padding(vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            InfoTab("Lignes", Icons.Default.Train) { selectedTab = 0 }
            InfoTab("Localisation", Icons.Default.Place) { selectedTab = 1 }
            InfoTab("Passagers", Icons.Default.Person) { selectedTab = 2 }
            InfoTab("Horaires", Icons.Default.Schedule) { selectedTab = 3 }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(16.dp)
        ) {
            when (selectedTab) {
                0 -> LignesContent(station.lines)
                1 -> Column {
                    Text(
                        text = "📍 ${station.name}",
                        color = Color.Black,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Adresse: ${station.address}",
                        color = Color.Black,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
                2 -> Text(
                    text = "👥 Nombre de passagers par jour : ${station.dailyPassengers}",
                    color = Color.Black
                )
                3 -> Column {
                    Text(
                        text = "⏰ Heures de pointe : ${station.peakHours}",
                        color = Color.Black,
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "🕒 Horaires d'ouverture : ${station.openingHours}",
                        color = Color.Black,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        }
    }
}

@Composable
fun InfoTab(
    text: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable(onClick = onClick)
            .padding(horizontal = 8.dp)
    ) {
        Icon(
            icon,
            contentDescription = text,
            tint = Color.White,
            modifier = Modifier.size(24.dp)
        )
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall,
            color = Color.White
        )
    }
}

@Composable
fun LignesContent(lines: List<Line>) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(4),
        contentPadding = PaddingValues(8.dp)
    ) {
        items(lines.size) { index ->
            val line = lines[index]
            val backgroundColor = getLineColor(line.name)

            Box(
                modifier = Modifier
                    .padding(4.dp)
                    .size(40.dp),
                contentAlignment = Alignment.Center
            ) {
                when (line.type) {
                    LineType.METRO -> {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .background(color = backgroundColor, shape = CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                line.name,
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.titleLarge
                            )
                        }
                    }
                    LineType.RER -> {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .background(color = backgroundColor, shape = MaterialTheme.shapes.medium),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                line.name,
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.titleLarge
                            )
                        }
                    }
                }
            }
        }
    }
}