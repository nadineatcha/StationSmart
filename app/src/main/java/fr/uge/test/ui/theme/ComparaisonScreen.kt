package fr.uge.test.ui.theme

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.CompareArrows
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import fr.uge.test.model.HourlyPrediction
import fr.uge.test.model.Station
import fr.uge.test.viewmodel.PredictionViewModel
import fr.uge.test.viewmodel.StationViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ComparaisonScreen(
    stationViewModel: StationViewModel = viewModel(),
    predictionViewModel: PredictionViewModel = viewModel(),
    onNavigateBack: () -> Unit = {}
) {
    val stations by stationViewModel.searchResults.collectAsState()
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Retour")
                    }
                },
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.AutoMirrored.Filled.CompareArrows,
                            contentDescription = "Comparaison",
                            modifier = Modifier.padding(end = 8.dp)
                        )
                        Text(
                            "Comparer les stations",
                            fontWeight = FontWeight.Bold
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF4285F4),
                    titleContentColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        ComparaisonContent(
            modifier = Modifier.padding(paddingValues),
            stations = stations,
            predictionViewModel = predictionViewModel,
            onStationSelected = { station ->
                scope.launch {
                    predictionViewModel.loadPredictions(station.id)
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ComparaisonContent(
    modifier: Modifier = Modifier,
    stations: List<Station>,
    predictionViewModel: PredictionViewModel = viewModel(),
    onStationSelected: (Station) -> Unit
) {
    var station1 by remember { mutableStateOf<Station?>(null) }
    var station2 by remember { mutableStateOf<Station?>(null) }

    // Utiliser des listes de prédictions distinctes pour chaque station
    val station1Predictions = remember { mutableStateListOf<HourlyPrediction>() }
    val station2Predictions = remember { mutableStateListOf<HourlyPrediction>() }

    val uiState by predictionViewModel.uiState.collectAsState()
    val hourlyPredictions by predictionViewModel.hourlyPredictions.collectAsState()

    // Effet pour la première station
    LaunchedEffect(station1) {
        val currentStation = station1
        if (currentStation != null) {
            predictionViewModel.loadPredictions(currentStation.id)
            station1Predictions.clear()
            station1Predictions.addAll(hourlyPredictions)
        }
    }

    // Effet pour la deuxième station
    LaunchedEffect(station2) {
        val currentStation = station2
        if (currentStation != null) {
            predictionViewModel.loadPredictions(currentStation.id)
            station2Predictions.clear()
            station2Predictions.addAll(hourlyPredictions)
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Sélection des stations
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            StationSelector(
                stations = stations,
                label = "Station 1",
                onStationSelected = { selectedStation ->
                    station1 = selectedStation
                    onStationSelected(selectedStation)
                },
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp)
            )
            StationSelector(
                stations = stations,
                label = "Station 2",
                onStationSelected = { selectedStation ->
                    station2 = selectedStation
                    onStationSelected(selectedStation)
                },
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 8.dp)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Carte pour le graphique de comparaison
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            elevation = CardDefaults.cardElevation(4.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "Évolution du Trafic Comparé",
                    fontWeight = FontWeight.Bold
                )

                val station1Name = station1?.name ?: ""
                val station2Name = station2?.name ?: ""

                if (station1 != null && station2 != null) {
                    Text(
                        "$station1Name vs $station2Name",
                        color = Color.Gray
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    when (uiState) {
                        is PredictionViewModel.UiState.Loading -> {
                            CircularProgressIndicator(
                                modifier = Modifier.padding(32.dp)
                            )
                        }
                        is PredictionViewModel.UiState.Error -> {
                            Text(
                                "Erreur lors du chargement des prédictions",
                                color = Color.Red
                            )
                        }
                        is PredictionViewModel.UiState.Success -> {
                            // Graphique de comparaison
                            TrafficComparisonChart(
                                station1Predictions = station1Predictions,
                                station2Predictions = station2Predictions,
                                station1Name = station1Name,
                                station2Name = station2Name
                            )
                        }
                    }
                } else {
                    Text(
                        "Sélectionnez deux stations pour comparer",
                        color = Color.Gray,
                        modifier = Modifier.padding(vertical = 48.dp)
                    )
                }
            }
        }

        // Statistiques de comparaison
        if (station1 != null && station2 != null) {
            // Utiliser des variables locales pour éviter les smart cast impossibles
            val s1 = station1!!
            val s2 = station2!!

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            s1.name,
                            fontWeight = FontWeight.Bold
                        )
                        Text("Trafic actuel: ${s1.currentTraffic}")
                        Text("Lignes: ${s1.lines}")
                        if (station1Predictions.isNotEmpty()) {
                            Text("Pic prévu: ${station1Predictions.maxByOrNull { it.count }?.count ?: 0} personnes")
                        }
                    }

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            s2.name,
                            fontWeight = FontWeight.Bold
                        )
                        Text("Trafic actuel: ${s2.currentTraffic}")
                        Text("Lignes: ${s2.lines}")
                        if (station2Predictions.isNotEmpty()) {
                            Text("Pic prévu: ${station2Predictions.maxByOrNull { it.count }?.count ?: 0} personnes")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TrafficComparisonChart(
    station1Predictions: List<HourlyPrediction>,
    station2Predictions: List<HourlyPrediction>,
    station1Name: String,
    station2Name: String
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        // Légende
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(end = 16.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .background(Color.Blue)
                )
                Text(
                    text = station1Name,
                    modifier = Modifier.padding(start = 4.dp)
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .background(Color.Red)
                )
                Text(
                    text = station2Name,
                    modifier = Modifier.padding(start = 4.dp)
                )
            }
        }

        // Graphique
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
        ) {
            val height = size.height
            val width = size.width
            val padding = 50f

            // Axes
            drawLine(
                color = Color.Black,
                start = Offset(padding, height - padding),
                end = Offset(width - padding, height - padding),
                strokeWidth = 2f
            )
            drawLine(
                color = Color.Black,
                start = Offset(padding, padding),
                end = Offset(padding, height - padding),
                strokeWidth = 2f
            )

            // Calculer la valeur maximale entre les deux ensembles de prédictions
            val maxValue = maxOf(
                station1Predictions.maxOfOrNull { it.count } ?: 0,
                station2Predictions.maxOfOrNull { it.count } ?: 0
            ).toFloat()

            // Heures communes pour l'axe X (fusionner les heures des deux stations)
            val allHours = (station1Predictions.map { it.time } + station2Predictions.map { it.time }).distinct().sorted()

            if (allHours.isNotEmpty() && maxValue > 0) {
                val hourStep = (width - 2 * padding) / allHours.size

                // Déterminer l'intervalle d'affichage pour les heures sur l'axe X
                // Plus il y a d'heures, plus on saute de valeurs pour éviter le chevauchement
                val displayEvery = when {
                    allHours.size > 15 -> 6
                    allHours.size > 10 -> 4
                    allHours.size > 5 -> 2
                    else -> 1
                }

                // Dessiner les lignes de repère et les heures
                allHours.forEachIndexed { index, hour ->
                    val x = padding + index * hourStep

                    // Dessiner une ligne de repère verticale légère pour chaque heure
                    drawLine(
                        color = Color.LightGray.copy(alpha = 0.3f),
                        start = Offset(x, height - padding),
                        end = Offset(x, padding),
                        strokeWidth = 0.5f
                    )

                    // N'afficher les libellés d'heures que tous les 'displayEvery' éléments
                    if (index % displayEvery == 0) {
                        // Marquer de façon plus visible les heures affichées
                        drawLine(
                            color = Color.Gray,
                            start = Offset(x, height - padding),
                            end = Offset(x, height - padding + 10),
                            strokeWidth = 1.5f
                        )

                        // Afficher l'heure avec un formatage adapté
                        drawContext.canvas.nativeCanvas.drawText(
                            hour,
                            x,
                            height - padding + 25,
                            android.graphics.Paint().apply {
                                color = android.graphics.Color.BLACK
                                textSize = 24f
                                textAlign = android.graphics.Paint.Align.CENTER
                            }
                        )
                    }
                }

                // Dessiner la courbe pour la station 1
                if (station1Predictions.isNotEmpty()) {
                    val path1 = Path().apply {
                        var first = true
                        station1Predictions.forEach { prediction ->
                            val hourIndex = allHours.indexOf(prediction.time)
                            if (hourIndex >= 0) {
                                val x = padding + hourIndex * hourStep
                                val y = height - padding - (prediction.count / maxValue) * (height - 2 * padding)

                                if (first) {
                                    moveTo(x, y)
                                    first = false
                                } else {
                                    lineTo(x, y)
                                }
                            }
                        }
                    }
                    drawPath(path1, color = Color.Blue, style = Stroke(width = 4f))

                    // Ajouter des points pour marquer les valeurs exactes
                    station1Predictions.forEach { prediction ->
                        val hourIndex = allHours.indexOf(prediction.time)
                        if (hourIndex >= 0) {
                            val x = padding + hourIndex * hourStep
                            val y = height - padding - (prediction.count / maxValue) * (height - 2 * padding)
                            drawCircle(
                                color = Color.Blue,
                                radius = 4f,
                                center = Offset(x, y)
                            )
                        }
                    }
                }

                // Dessiner la courbe pour la station 2
                if (station2Predictions.isNotEmpty()) {
                    val path2 = Path().apply {
                        var first = true
                        station2Predictions.forEach { prediction ->
                            val hourIndex = allHours.indexOf(prediction.time)
                            if (hourIndex >= 0) {
                                val x = padding + hourIndex * hourStep
                                val y = height - padding - (prediction.count / maxValue) * (height - 2 * padding)

                                if (first) {
                                    moveTo(x, y)
                                    first = false
                                } else {
                                    lineTo(x, y)
                                }
                            }
                        }
                    }
                    drawPath(path2, color = Color.Red, style = Stroke(width = 4f))

                    // Ajouter des points pour marquer les valeurs exactes
                    station2Predictions.forEach { prediction ->
                        val hourIndex = allHours.indexOf(prediction.time)
                        if (hourIndex >= 0) {
                            val x = padding + hourIndex * hourStep
                            val y = height - padding - (prediction.count / maxValue) * (height - 2 * padding)
                            drawCircle(
                                color = Color.Red,
                                radius = 4f,
                                center = Offset(x, y)
                            )
                        }
                    }
                }
            }
        }

        // Heures sur l'axe X
        Text(
            text = "Heures de la journée",
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            textAlign = TextAlign.Center
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StationSelector(
    stations: List<Station>,
    label: String,
    onStationSelected: (Station) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    var selectedStation by remember { mutableStateOf<Station?>(null) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = modifier
    ) {
        TextField(
            value = selectedStation?.name ?: "",
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            placeholder = { Text("Sélectionner une station") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            colors = ExposedDropdownMenuDefaults.textFieldColors(),
            modifier = Modifier.menuAnchor()
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            stations.forEach { station ->
                DropdownMenuItem(
                    text = { Text(station.name) },
                    onClick = {
                        selectedStation = station
                        onStationSelected(station)
                        expanded = false
                    }
                )
            }
        }
    }
}