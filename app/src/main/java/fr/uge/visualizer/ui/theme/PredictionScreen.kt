package fr.uge.visualizer.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fr.uge.visualizer.viewmodel.PredictionViewModel
import android.widget.Toast
import androidx.compose.ui.platform.LocalContext

@Composable
fun PredictionScreen(
    viewModel: PredictionViewModel,
    onNavigateToNotifications: () -> Unit = {},
    onNavigateToStations: () -> Unit = {}
) {
    val context = LocalContext.current
    val stationInfo by viewModel.stationInfo.collectAsState()
    val predictions by viewModel.hourlyPredictions.collectAsState()
    val uiState by viewModel.uiState.collectAsState()

    // Observer le message toast
    val toastMessage by viewModel.toastMessage.collectAsState()
    LaunchedEffect(toastMessage) {
        toastMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
        }
    }

    Scaffold(
        topBar = {
            // En-tête
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF4F46E5))
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Partie gauche avec titre et flèche retour
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Retour",
                            tint = Color.White
                        )
                        Column(modifier = Modifier.padding(start = 8.dp)) {
                            Text(
                                text = "Prédictions",
                                color = Color.White,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold
                            )

                            Text(
                                text = "Station ${stationInfo.name}",
                                color = Color(0xFFE0E7FF),
                                fontSize = 14.sp
                            )

                            Text(
                                text = "Lignes ${stationInfo.lines}",
                                color = Color(0xFFE0E7FF),
                                fontSize = 14.sp
                            )
                        }
                    }

                    // Ajouter l'icône de notification
                    Icon(
                        imageVector = Icons.Default.Notifications,
                        contentDescription = "Notifications",
                        tint = Color.White,
                        modifier = Modifier
                            .clickable { onNavigateToNotifications() }
                            .size(24.dp)
                    )
                }
            }
        },
        floatingActionButton = {
            // Bouton pour accéder aux stations proches
            FloatingActionButton(
                onClick = { onNavigateToStations() },
                containerColor = Color(0xFF4F46E5)
            ) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = "Stations à proximité",
                    tint = Color.White
                )
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFFF3F4F6))
        ) {
            when (val state = uiState) {
                is PredictionViewModel.UiState.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .size(64.dp)
                            .align(Alignment.Center),
                        color = Color(0xFF4F46E5)
                    )
                }

                is PredictionViewModel.UiState.Error -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = "Erreur",
                            tint = Color.Red,
                            modifier = Modifier.size(64.dp)
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "Erreur de chargement des données",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.SemiBold
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = state.message,
                            color = Color.Gray
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = { viewModel.refresh() },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4F46E5))
                        ) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = "Réessayer",
                                tint = Color.White
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Réessayer", color = Color.White)
                        }
                    }
                }

                is PredictionViewModel.UiState.Success -> {
                    LazyColumn {
                        // Indicateur de données de secours si nécessaire
                        if (state.isFallbackData) {
                            item {
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    shape = RoundedCornerShape(8.dp),
                                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF4E5))
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(16.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Warning,
                                            contentDescription = "Avertissement",
                                            tint = Color(0xFFFF9800)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = "Données estimées. Les données réelles ne sont pas disponibles actuellement.",
                                            color = Color(0xFF875000)
                                        )
                                    }
                                }
                            }
                        }

                        // Statistiques rapides
                        item {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                // Carte d'affluence actuelle
                                Card(
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(16.dp),
                                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                                ) {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(16.dp)
                                    ) {
                                        Text(
                                            text = "Affluence actuelle",
                                            color = Color.Gray,
                                            fontSize = 14.sp
                                        )

                                        Text(
                                            text = "${stationInfo.currentTraffic}",
                                            fontSize = 24.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF4F46E5)
                                        )

                                        Row(
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.ArrowUpward,
                                                contentDescription = "Tendance à la hausse",
                                                tint = Color.Green,
                                                modifier = Modifier.size(16.dp)
                                            )

                                            Text(
                                                text = "+${stationInfo.trend}% vs moyenne",
                                                fontSize = 12.sp,
                                                color = Color.Green
                                            )
                                        }
                                    }
                                }

                                // Carte pic attendu
                                Card(
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(16.dp),
                                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                                ) {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(16.dp)
                                    ) {
                                        Text(
                                            text = "Pic attendu",
                                            color = Color.Gray,
                                            fontSize = 14.sp
                                        )

                                        Text(
                                            text = "${stationInfo.peakTraffic}",
                                            fontSize = 24.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF4F46E5)
                                        )

                                        Text(
                                            text = "à ${stationInfo.peakTime}",
                                            fontSize = 12.sp,
                                            color = Color.Gray
                                        )
                                    }
                                }
                            }
                        }

                        // Prédictions horaires
                        item {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                shape = RoundedCornerShape(16.dp),
                                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text(
                                        text = "Prévisions horaires",
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 18.sp,
                                        modifier = Modifier.padding(bottom = 16.dp)
                                    )

                                    predictions.forEach { prediction ->
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(vertical = 8.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = prediction.time,
                                                modifier = Modifier.width(48.dp),
                                                fontSize = 14.sp
                                            )

                                            Box(
                                                modifier = Modifier
                                                    .weight(1f)
                                                    .height(8.dp)
                                                    .background(
                                                        Color(0xFFEEF2FF),
                                                        RoundedCornerShape(4.dp)
                                                    )
                                            ) {
                                                Box(
                                                    modifier = Modifier
                                                        .fillMaxHeight()
                                                        .fillMaxWidth(prediction.percentageCapacity / 100f)
                                                        .background(
                                                            Color(0xFF4F46E5),
                                                            RoundedCornerShape(4.dp)
                                                        )
                                                )
                                            }

                                            Row(
                                                modifier = Modifier
                                                    .width(80.dp)
                                                    .padding(start = 8.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Text(
                                                    text = prediction.count.toString(),
                                                    color = Color.Gray,
                                                    fontSize = 14.sp
                                                )

                                                if (prediction.trend == "up") {
                                                    Icon(
                                                        imageVector = Icons.Default.ArrowUpward,
                                                        contentDescription = "Tendance à la hausse",
                                                        tint = Color.Green,
                                                        modifier = Modifier
                                                            .size(16.dp)
                                                            .padding(start = 4.dp)
                                                    )
                                                } else {
                                                    Icon(
                                                        imageVector = Icons.Default.ArrowDownward,
                                                        contentDescription = "Tendance à la baisse",
                                                        tint = Color.Red,
                                                        modifier = Modifier
                                                            .size(16.dp)
                                                            .padding(start = 4.dp)
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        // Bouton de rafraîchissement
                        item {
                            Button(
                                onClick = { viewModel.refresh() },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4F46E5))
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Refresh,
                                    contentDescription = "Rafraîchir",
                                    tint = Color.White
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Rafraîchir", color = Color.White)
                            }
                        }
                    }
                }
            }
        }
    }
}