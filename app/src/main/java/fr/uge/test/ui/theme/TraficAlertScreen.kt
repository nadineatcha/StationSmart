package fr.uge.test.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.Surface
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.material3.TextField
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController



@Preview(showBackground = true)
@Composable
fun TraficAlertPage(navController: NavController = rememberNavController()) {
    Scaffold(
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFFFD600)) // Yellow background
                    .padding(16.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.Black,
                        modifier = Modifier
                            .clickable { navController.navigate("articles") }
                            .padding(end = 8.dp)
                            .size(24.dp)
                    )
                    Text(
                        text = "Trafic Alert",
                        color = Color.Black,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .padding(paddingValues),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Top
        ) {
            // Traffic Alert Content
            Text(
                text = "⚠ TRAFIC ALERT : RER B",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text("📍 Secteur concerné : Entre les gares Antony et Massy-Palaiseau")
            Spacer(modifier = Modifier.height(4.dp))
            Text("⏰ Période : Lundi 17 février 2025, de 9h30 à 15h00")
            Spacer(modifier = Modifier.height(4.dp))
            Text("🚧 Motif : Travaux de maintenance sur les voies")
            Spacer(modifier = Modifier.height(8.dp))
            Text("📢 Impact :")
            Spacer(modifier = Modifier.height(4.dp))
            Text("Circulation interrompue entre Antony et Massy-Palaiseau pendant la période indiquée.")
            Text("Des bus de remplacement seront mis en place pour assurer la liaison entre les deux gares.")
            Text("Des retards et perturbations sont à prévoir sur l’ensemble de la ligne.")
            Spacer(modifier = Modifier.height(8.dp))
            Text("✅ Recommandations :")
            Spacer(modifier = Modifier.height(4.dp))
            Text("Si possible, privilégiez un itinéraire alternatif via les lignes RER C ou Ligne 6 du métro.")
            Text("Consultez l’application Visualizer pour des prédictions et des alternatives en temps réel.")
            Text("Planifiez votre trajet à l’avance et prenez en compte les délais supplémentaires.")
        }
    }
}