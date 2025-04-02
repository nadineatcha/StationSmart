package fr.uge.test.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
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
fun ConfidentialiteScreen(navController: NavController) {
    Scaffold(
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.Red) // Background color
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
                        text = "Confidentialité",
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    ) { paddingValues ->
        // Content of the page
        val scrollState = rememberScrollState()

        // Use a Box to contain the Column and enable scrolling
        Box(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .verticalScroll(scrollState) // Enable scrolling
        ) {
            Column(
                modifier = Modifier.fillMaxWidth().padding(all = 12.dp), // Fill the width of the Box
                horizontalAlignment = Alignment.Start, // Align left for better readability
                verticalArrangement = Arrangement.Top
            ) {
                // Complete text content goes here
                Text(text = "Politique de Confidentialité de Station Smart", fontWeight = FontWeight.Bold, fontSize = 24.sp)
                Text(text = "Chez Station Smart, la confidentialité et la sécurité de vos informations personnelles sont d'une importance capitale. Cette politique de confidentialité a pour objectif de vous expliquer comment nous collectons, utilisons, stockons et protégeons vos données lorsque vous utilisez notre application mobile dédiée à la prédiction de la charge et du trafic dans les gares d'Île-de-France.\n")

                Text(text = "1. Informations collectées", fontWeight = FontWeight.Bold)
                Text(text = "Lorsque vous utilisez l'application Station Smart, nous pouvons collecter les types d'informations suivants :\n")
                Text(text = "Informations personnelles : Nous ne collectons généralement pas d'informations personnelles vous identifiant directement. Toutefois, si vous vous inscrivez à des fonctionnalités personnalisées, nous pourrions collecter votre nom, adresse e-mail et autres informations nécessaires à la gestion de votre compte.\n")
                Text(text = "Données de localisation : Pour vous fournir des informations précises sur la charge et le trafic dans les gares d'Île-de-France, nous collectons des données de localisation en temps réel (géolocalisation). Ces données nous permettent de vous afficher les informations les plus pertinentes en fonction de votre position actuelle.\n")
                Text(text = "Informations d'utilisation : Nous collectons des informations techniques sur l’utilisation de l’application, telles que le type de votre appareil, le système d'exploitation, les pages visitées, la durée d’utilisation et d'autres interactions avec l’application. Cela nous permet d'améliorer les fonctionnalités de l'application.\n")

                Text(text = "2. Utilisation de vos informations", fontWeight = FontWeight.Bold)
                Text(text = "Les informations collectées sont utilisées dans les buts suivants :\n")
                Text(text = "Prédiction du trafic et de la charge : Nous utilisons les données de géolocalisation pour prédire en temps réel l’afflux et la charge dans les gares d'Île-de-France, afin de vous fournir des informations fiables et pertinentes.\n")
                Text(text = "Amélioration de l’application : Nous utilisons les informations d'utilisation pour analyser la performance de l'application et l'améliorer en continu. Cela inclut l'optimisation des prévisions de trafic et de la précision de la géolocalisation.\n")
                Text(text = "Notifications personnalisées : En fonction de vos préférences et de votre emplacement, nous pouvons vous envoyer des notifications concernant les changements de trafic ou de charge dans les gares, afin que vous puissiez mieux planifier vos trajets.\n")

                Text(text = "3. Partage des informations", fontWeight = FontWeight.Bold)
                Text(text = "Nous nous engageons à ne pas partager vos informations personnelles avec des tiers, à l'exception des cas suivants :\n")
                Text(text = "Partenaires de service : Nous pouvons partager des informations avec des prestataires de services qui nous aident à gérer l’application (par exemple, services d'hébergement, analyse de données). Ces partenaires n'ont accès à vos informations que pour effectuer ces services et sont tenus de les protéger.\n")
                Text(text = "Exigences légales : Nous pouvons être amenés à divulguer vos informations si cela est nécessaire pour se conformer à des obligations légales, pour répondre à une procédure judiciaire ou pour protéger nos droits et ceux des utilisateurs.\n")

                Text(text = "4. Sécurité de vos informations", fontWeight = FontWeight.Bold)
                Text(text = "Nous utilisons des protocoles de sécurité rigoureux pour protéger vos données personnelles et garantir leur confidentialité. Cependant, bien que nous prenions toutes les précautions possibles, aucune méthode de transmission sur Internet n'est totalement sécurisée, et nous ne pouvons garantir la sécurité absolue des données transmises.\n")

                Text(text = "5. Vos droits", fontWeight = FontWeight.Bold)
                Text(text = "Conformément à la législation applicable, vous disposez des droits suivants concernant vos informations personnelles :\n")
                Text(text = "Accès : Vous pouvez demander à consulter les informations que nous détenons à votre sujet.\n")
                Text(text = "Rectification : Vous pouvez demander la correction de toute information incorrecte ou incomplète.\n")
                Text(text = "Suppression : Vous avez le droit de demander la suppression de vos données personnelles, sous réserve des obligations légales et des besoins de conservation.\n")
                Text(text = "Opposition et retrait du consentement : Vous pouvez à tout moment vous opposer à l'utilisation de vos données pour certaines finalités ou retirer votre consentement pour la géolocalisation en modifiant les paramètres de votre appareil.\n")
                Text(text = "Pour exercer ces droits, veuillez nous contacter à l'adresse suivante : [adresse e-mail ou méthode de contact].\n")

                Text(text = "6. Modifications de cette politique", fontWeight = FontWeight.Bold)
                Text(text = "Nous nous réservons le droit de modifier cette politique de confidentialité à tout moment. Les mises à jour seront publiées directement dans l'application, et nous vous informerons de toute modification significative. Nous vous encourageons à consulter régulièrement cette page pour rester informé des changements.\n")

                Text(text = "7. Contact", fontWeight = FontWeight.Bold)
                Text(text = "Si vous avez des questions concernant cette politique de confidentialité ou la manière dont vos données sont traitées, veuillez nous contacter à l'adresse suivante : smartstation@gmail.com.\n")
            }
        }
    }
}