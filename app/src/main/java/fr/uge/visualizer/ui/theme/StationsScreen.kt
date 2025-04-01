// StationsScreen.kt dans fr.uge.visualizer.ui
package fr.uge.visualizer.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fr.uge.visualizer.model.Station
import fr.uge.visualizer.viewmodel.StationViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StationsScreen(
    viewModel: StationViewModel,
    onNavigateBack: () -> Unit = {},
    onStationSelected: (String) -> Unit = {}
) {
    val stations by viewModel.nearbyStations.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Stations à proximité") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Retour"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.refreshNearbyStations() }) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Rafraîchir"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF4F46E5),
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White,
                    actionIconContentColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFFF3F4F6))
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = Color(0xFF4F46E5)
                )
            } else if (stations.isEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = Color(0xFF4F46E5)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Aucune station trouvée à proximité",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Assurez-vous que la localisation est activée",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Button(
                        onClick = { viewModel.refreshNearbyStations() },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF4F46E5)
                        )
                    ) {
                        Text("Réessayer")
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    items(stations) { station ->
                        StationCard(
                            station = station,
                            onClick = { onStationSelected(station.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun StationCard(
    station: Station,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.LocationOn,
                contentDescription = null,
                tint = Color(0xFF4F46E5),
                modifier = Modifier
                    .size(32.dp)
                    .padding(end = 16.dp)
            )

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = station.name,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                Text(
                    text = "Lignes ${station.lines}",
                    color = Color.Gray,
                    fontSize = 14.sp
                )
            }

            if (station.distance != null) {
                Text(
                    text = formatDistance(station.distance),
                    fontSize = 14.sp,
                    color = Color(0xFF4F46E5)
                )
            }
        }
    }
}

fun formatDistance(distance: Float): String {
    return when {
        distance < 1000 -> "${distance.toInt()}m"
        else -> String.format("%.1fkm", distance / 1000)
    }
}