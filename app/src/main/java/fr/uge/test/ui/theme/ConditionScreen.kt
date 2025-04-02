package fr.uge.test.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@Composable
fun ConditionScreen(navController: NavController) {
    Scaffold(
        topBar = {
            // Custom Top Bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF4285F4))
                    .padding(16.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White,
                        modifier = Modifier
                            .clickable { navController.navigate("settings") }
                            .padding(end = 8.dp)
                            .size(24.dp)
                    )
                    Text(
                        text = "Conditions",
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    ) { paddingValues ->
        // Using ScrollableColumn to allow text scrolling
        val scrollState = rememberScrollState()

        Column(
            modifier = Modifier
                .padding(paddingValues)
                .verticalScroll(scrollState)
                .padding(16.dp)
        ) {
            Text(
                text = "Conditions Générales d'Utilisation de l'Application Visualizer",
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "Date de mise à jour : 28 mars 2025", fontSize = 16.sp)

            // Detailed text sections
            val sections = listOf(
                "1. Introduction" to "Bienvenue sur Visualizer, une application dédiée à la visualisation du trafic dans les gares d'Île-de-France ainsi qu'à la prédiction des perturbations. En utilisant l'application, vous acceptez les présentes Conditions Générales d'Utilisation (CGU). Veuillez les lire attentivement.",
                "2. Description de l'application" to "Visualizer propose les services suivants :\n\nConsultation en temps réel des conditions de trafic dans les gares d'Île-de-France.\nPrédictions des perturbations basées sur des données historiques et des algorithmes d'intelligence artificielle.\nNotifications personnalisées pour les trajets enregistrés.",
                "3. Accès à l'application" to "L'accès à l'application est gratuit pour tous les utilisateurs.",
                "4. Utilisation de l'application" to "L'utilisateur s'engage à utiliser l'application uniquement.",
                "5. Données personnelles" to "Visualizer collecte des données personnelles conformément à sa Politique de Confidentialité.",
                "6. Limitation de responsabilité" to "Visualizer ne peut être tenu responsable en cas d'erreur.",
                "7. Propriété intellectuelle" to "Tous les contenus de l'application sont protégés.",
                "8. Modifications des CGU" to "Visualizer se réserve le droit de modifier ces CGU.",
                "9. Droit applicable et litiges" to "Les présentes CGU sont régies par le droit français. En cas de litige, les parties s'engagent à rechercher une solution amiable avant de porter l'affaire devant les tribunaux compétents de Paris.",
                "10. Contact" to "Pour toute question relative aux présentes CGU ou à l'application..."
            )

            sections.forEach { (title, content) ->
                Spacer(modifier = Modifier.height(16.dp))
                Text(text = title, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Text(text = content, fontSize = 16.sp)
            }

            Spacer(modifier = Modifier.height(16.dp))
            Text(text = "Pour toute question, veuillez contacter : support@visualizer.fr", fontSize = 16.sp)
        }

        // Floating Action Button
        Box(modifier = Modifier.fillMaxSize()) {
            FloatingActionButton(
                onClick = { /* Action ici */ },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp)
            ) {
                Icon(Icons.Filled.Edit, contentDescription = "Modifier")
            }
        }
    }
}