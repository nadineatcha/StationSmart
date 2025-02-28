package fr.uge.visualizer.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.ArrowDownward
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
import fr.uge.visualizer.model.HourlyPrediction
import fr.uge.visualizer.viewmodel.PredictionViewModel

@Composable
fun PredictionScreen(viewModel: PredictionViewModel) {
    val stationInfo by viewModel.stationInfo.collectAsState()
    val predictions by viewModel.hourlyPredictions.collectAsState()

    Scaffold(
        topBar = {
            // En-tête
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF4F46E5))
                    .padding(16.dp)
            ) {
                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Retour",
                            tint = Color.White
                        )
                        Text(
                            text = "Prédictions",
                            color = Color.White,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }

                    Text(
                        text = "Station ${stationInfo.name}",
                        color = Color(0xFFE0E7FF),
                        fontSize = 14.sp,
                        modifier = Modifier.padding(top = 4.dp)
                    )

                    Text(
                        text = "Lignes ${stationInfo.lines}",
                        color = Color(0xFFE0E7FF),
                        fontSize = 14.sp
                    )
                }
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFFF3F4F6))
        ) {
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
                                        .background(Color(0xFFEEF2FF), RoundedCornerShape(4.dp))
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxHeight()
                                            .fillMaxWidth(prediction.percentage / 100f)
                                            .background(Color(0xFF4F46E5), RoundedCornerShape(4.dp))
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
        }
    }
}