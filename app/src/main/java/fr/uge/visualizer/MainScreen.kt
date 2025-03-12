package fr.uge.visualizer

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(navController: NavController) {
    var termsAccepted by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Legal") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            Text(
                text = "Termes et règles d'utilisation",
                style = MaterialTheme.typography.headlineSmall
            )

            Spacer(modifier = Modifier.height(16.dp))

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .background(Color(0xFFF5F5F5), shape = MaterialTheme.shapes.medium)
                    .padding(16.dp)
            ) {
                Text(
                    text = "Lorem ipsum dolor sit amet consectetur. Bibendum quis ipsum ut neque mattis feugiat facilisi. Urna cursus consectetur aliquam sit tristique tincidunt lacinia. Sapien dis eget egestas tellus sit dictumst suspendisse dignissim mattis et lectus aliquam. Tortor in aenean turpis odio eleifend elementum. Rhoncus vel integer aliquet ame enim interdum scelerisque sed quis. Porttitor eget bibendum amet sollicitudin venenatis quam ut mattis purus. Dictum quis in tellus eget dignissim amet sollicitudin venenatis quam ut mattis purus. Dictum quis in tellus eget posuerit lectus mi. Id mauris et ultrices fringilla urna egestas enim.\n\nLorem ipsum dolor sit amet consectetur. Bibendum quis ipsum ut neque mattis feugiat facilisi. Urna cursus consectetur aliquam sit tristique tincidunt lacinia. Sapien dis eget egestas tellus sit",
                    modifier = Modifier.verticalScroll(rememberScrollState())
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = termsAccepted,
                    onCheckedChange = { termsAccepted = it }
                )
                Text("En cochant cette case, je reconnais avoir pris connaissance des conditions générales et les accepter")
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    if (termsAccepted) {
                        navController.navigate("splash_screen")
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = termsAccepted,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Blue
                )
            ) {
                Text("Suivant")
            }
        }
    }
}