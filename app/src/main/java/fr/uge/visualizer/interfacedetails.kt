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
import fr.uge.visualizer.ui.theme.MonDetailsTheme
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker

// Définition des types de lignes
enum class LineType {
    RER,
    METRO
}

// Classe pour représenter une ligne avec son type
data class Line(
    val name: String,
    val type: LineType // Type de ligne (METRO ou RER)
)

// Classe Station avec une liste de lignes et des horaires d'ouverture
data class Station(
    val name: String,
    val location: GeoPoint,
    val address: String, // Adresse complète
    val lines: List<Line>, // Liste de lignes avec leur type
    val dailyPassengers: Int,
    val peakHours: String, // Heures de pointe
    val openingHours: String // Horaires d'ouverture
)

// Fonction pour obtenir la couleur spécifique à chaque ligne
fun getLineColor(line: String): Color {
    return when (line) {
        "1" -> Color(0xFFF3D03E) // Jaune
        "2" -> Color(0xFF0065AE) // Bleu
        "3" -> Color(0xFF9B642B) // Marron
        "4" -> Color(0xFFBE418D) // Rose
        "5" -> Color(0xFFFF7F27) // Orange
        "6" -> Color(0xFF6EC4B0) // Vert clair
        "7" -> Color(0xFFF59EB6) // Rose clair
        "8" -> Color(0xFFC7A8D9) // Violet
        "9" -> Color(0xFFD1C5A6) // Beige
        "10" -> Color(0xFFE3B32A) // Or
        "11" -> Color(0xFF8E6538) // Marron foncé
        "12" -> Color(0xFF007852) // Vert foncé
        "13" -> Color(0xFF98D4E2) // Bleu clair
        "14" -> Color(0xFF662483) // Violet foncé
        "A" -> Color(0xFFE2231A) // Rouge
        "B" -> Color(0xFF7BA3DC) // Bleu ciel
        "C" -> Color(0xFFF9A01B) // Orange
        "D" -> Color(0xFF00A74A) // Vert
        "E" -> Color(0xFFD48FCD) // Rose
        else -> Color.Gray // Couleur par défaut
    }
}

// Activité principale
class interfacedetails : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Configuration d'OSMDroid
        Configuration.getInstance().load(this, getSharedPreferences("osm", MODE_PRIVATE))

        setContent {
            MonDetailsTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color.White
                ) {
                    StationFinderApp()
                }
            }
        }
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
                icon = {
                    Icon(
                        icons[index],
                        contentDescription = item
                    )
                },
                label = { Text(item) },
                selected = selectedItem == index,
                onClick = {
                    selectedItem = index
                    // Ajoutez ici la logique de navigation ou d'action pour chaque élément
                    when (index) {
                        0 -> { /* Action pour Accueil */ }
                        1 -> { /* Action pour Notifications */ }
                        2 -> { /* Action pour Paramètres */ }
                    }
                }
            )
        }
    }
}

@Composable
fun StationFinderApp() {
    val stations = remember {
        listOf(
            Station(
                "Châtelet-Les Halles",
                GeoPoint(48.8620, 2.3472),
                "1 Place Marguerite de Navarre, 75001 Paris",
                listOf(
                    Line("1", LineType.METRO),
                    Line("4", LineType.METRO),
                    Line("7", LineType.METRO),
                    Line("11", LineType.METRO),
                    Line("14", LineType.METRO),
                    Line("A", LineType.RER),
                    Line("B", LineType.RER),
                    Line("D", LineType.RER)
                ),
                750000,
                "7h-9h | 17h-19h",
                "Ouvert tous les jours de 5h30 à 1h00"
            ),
            Station(
                "Gare du Nord",
                GeoPoint(48.8809, 2.3553),
                "18 Rue de Dunkerque, 75010 Paris",
                listOf(
                    Line("2", LineType.METRO),
                    Line("4", LineType.METRO),
                    Line("5", LineType.METRO),
                    Line("B", LineType.RER),
                    Line("D", LineType.RER),
                    Line("E", LineType.RER)
                ),
                700000,
                "7h-9h | 17h-19h",
                "Ouvert tous les jours de 5h00 à 1h30"
            )
        )
    }

    var searchQuery by remember { mutableStateOf("") }
    var selectedStation by remember { mutableStateOf<Station?>(null) }
    var showInfo by remember { mutableStateOf(false) }
    var showSuggestions by remember { mutableStateOf(false) }

    // Filtrer les stations selon la recherche
    val filteredStations = stations.filter {
        it.name.contains(searchQuery, ignoreCase = true)
    }

    Scaffold(
        bottomBar = { BottomNavigationBar() }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color.White)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .background(Color.White)
            ) {
                // Carte interactive OpenStreetMap
                OsmMapView(
                    stations = if (selectedStation != null) listOf(selectedStation!!) else emptyList(),
                    selectedStation = selectedStation,
                    onStationSelected = { station ->
                        selectedStation = station
                        showInfo = true
                        showSuggestions = false
                        searchQuery = station.name
                    },
                    modifier = Modifier.fillMaxSize()
                )

                // Conteneur pour la barre de recherche et les suggestions
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .zIndex(1f)
                ) {
                    // Barre de recherche
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

                    // Liste des suggestions
                    if (showSuggestions && filteredStations.isNotEmpty()) {
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(max = 200.dp),
                            color = Color.White,
                            shadowElevation = 4.dp
                        ) {
                            LazyColumn {
                                items(filteredStations.size) { index ->
                                    val station = filteredStations[index]
                                    StationSuggestionItem(
                                        station = station,
                                        onClick = {
                                            selectedStation = station
                                            showInfo = true
                                            showSuggestions = false
                                            searchQuery = station.name
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Bouton pour afficher les informations de la station
            if (selectedStation != null) {
                Button(
                    onClick = { showInfo = !showInfo },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3F51B5)),
                    shape = MaterialTheme.shapes.medium
                ) {
                    Text("Informations sur la station", color = Color.White)
                }

                if (showInfo) {
                    InfoPanel(station = selectedStation!!)
                }
            }
        }
    }
}

// Barre de recherche
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

// Carte OpenStreetMap
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
                controller.setCenter(GeoPoint(48.8566, 2.3522)) // Centre sur Paris par défaut
            }
        },
        update = { mapView ->
            mapView.overlays.clear() // Supprime les anciens marqueurs
            stations.forEach { station ->
                val marker = Marker(mapView).apply {
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
                }
                mapView.overlays.add(marker)
            }

            // Centrer sur la station sélectionnée ou sur Paris
            val centerPoint = selectedStation?.location ?: GeoPoint(48.8566, 2.3522)
            mapView.controller.apply {
                setZoom(15.0)
                animateTo(centerPoint, 15.0, 1000L) // Animation fluide
            }

            mapView.invalidate() // Rafraîchit la carte
        }
    )
}

// Élément de suggestion de station
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
        Icon(
            Icons.Default.LocationOn,
            contentDescription = "Location",
            tint = Color(0xFF3F51B5)
        )
    }
}

// Panneau d'informations
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

// Onglet d'informations
@Composable
fun InfoTab(text: String, icon: ImageVector, onClick: () -> Unit) {
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

            // Définir la forme en fonction du type de ligne
            Box(
                modifier = Modifier
                    .padding(4.dp)
                    .size(40.dp),
                contentAlignment = Alignment.Center
            ) {
                when (line.type) {
                    LineType.METRO -> {
                        // Ligne de métro avec un cercle
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .background(
                                    color = backgroundColor,
                                    shape = CircleShape
                                ),
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
                        // Ligne RER avec un carré aux coins arrondis
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .background(
                                    color = backgroundColor,
                                    shape = MaterialTheme.shapes.medium
                                ),
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