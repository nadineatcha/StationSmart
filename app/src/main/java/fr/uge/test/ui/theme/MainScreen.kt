package fr.uge.test.ui.theme

import fr.uge.test.OSMMapView
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.collectAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import fr.uge.test.viewmodel.StationViewModel
import fr.uge.test.model.Station
import fr.uge.test.model.TrafficLevel
import fr.uge.test.model.StationInfo
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue



@Composable
fun MainScreen(
    modifier: Modifier = Modifier,
    viewModel: StationViewModel = viewModel()
) {
    var searchQuery by remember { mutableStateOf("") }
    val stations by viewModel.searchResults.collectAsState()
    val selectedStation by viewModel.selectedStation.collectAsState()
    val stationInfo by viewModel.stationInfo.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadAllStations()
    }

    Column(modifier = modifier.fillMaxSize()) {
        // Barre de recherche
        TextField(
            value = searchQuery,
            onValueChange = {
                searchQuery = it
                viewModel.searchStations(it)
            },
            placeholder = { Text("Rechercher une station...") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        )

        // Carte OSM
        OSMMapView(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp),
            viewModel = viewModel,
            onMarkerClick = { station: Station -> // <-- Type explicite ici
                viewModel.selectStation(station)
            }
        )

        // Afficher un message d'erreur si nécessaire
        error?.let {
            Text(
                text = it,
                color = Color.Red,
                modifier = Modifier.padding(8.dp)
            )
        }

        // Afficher un indicateur de chargement si nécessaire
        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }

        // Liste des stations filtrées

        // Dans MainScreen.kt
        LazyColumn(modifier = Modifier.weight(1f)) {
            items(
                items = stations,
                key = { it.id }
            ) { station: Station ->  // <-- Spécification explicite du type ici
                StationListItem(
                    station = station,
                    isSelected = selectedStation?.id == station.id,
                    trafficLevel = viewModel.getTrafficLevel(station),
                    onClick = { viewModel.selectStation(station) }
                )
            }
        }
        // Carte de la station sélectionnée
        selectedStation?.let { station ->
            stationInfo?.let { info ->
                StationDetailCard(info = info)
            }
        }
    }
}

@Composable
fun StationListItem(
    station: Station,
    isSelected: Boolean,
    trafficLevel: TrafficLevel,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp)
            .clickable(onClick = onClick),
        elevation = if (isSelected) 8.dp else 2.dp,
        backgroundColor = if (isSelected) Color(0xFFE3F2FD) else MaterialTheme.colors.surface
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Indicateur de trafic
            Box(
                modifier = Modifier
                    .size(16.dp)
                    .background(
                        color = when (trafficLevel) {
                            TrafficLevel.LOW -> Color.Green
                            TrafficLevel.MEDIUM -> Color(0xFFFFD700) // Or
                            TrafficLevel.HIGH -> Color.Red
                        },
                        shape = RoundedCornerShape(50)
                    )
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column {
                Text(
                    text = station.name,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )

                Text(
                    text = "Lignes: ${station.lines}",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }
        }
    }
}

@Composable
fun StationDetailCard(info: StationInfo) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = 4.dp
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = info.name,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("Trafic actuel")
                    Text(
                        text = "${info.currentTraffic} personnes",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }

                Column {
                    Text("Pic attendu")
                    Text(
                        text = "${info.peakTraffic} à ${info.peakTime}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Indicateur de tendance
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Tendance: ")
                Text(
                    text = if (info.trend > 0) "+${info.trend}%" else "${info.trend}%",
                    color = if (info.trend > 0) Color.Green else Color.Red,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
