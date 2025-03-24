package fr.uge.visualizer.ui.theme

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ComparaisonScreen() {
    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF176BFF) // Change to desired color
                ),
                title = {
                    Text(
                        "Comparer les stations",
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                })
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(24.dp)
        ) {
            // Row with Two Select Menus
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                CustomDropdownMen("Station 1", listOf("Option 1", "Option 2", "Option 3"))
                CustomDropdownMen("Station 2", listOf("Option 1", "Option 2", "Option 3"))

            }

            Spacer(modifier = Modifier.height(24.dp))
            Chart()
        }
    }
}

@Composable
fun CustomDropdownMen(label: String, options: List<String>) {
    var expanded by remember { mutableStateOf(false) }
    var selectedOption by remember { mutableStateOf(label) }

    Box {
        TextButton(
            onClick = { expanded = true },
            colors = ButtonDefaults.textButtonColors(
                containerColor = Color.Transparent,
                contentColor = Color.Black
            ),
            modifier = Modifier.border(1.dp, Color.Black).padding(horizontal = 24.dp, vertical = 4.dp)
        ) {
            Text(selectedOption, fontSize = 16.sp)
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            options.forEach { option ->
                DropdownMenuItem(text = { Text(option) }, onClick = {
                    selectedOption = option
                    expanded = false
                })
            }
        }
    }
}

@Composable
fun Chart(){
    Column(Modifier
        .border(1.dp, Color(0xFFD3D3D3)) // Add border here
        .padding(16.dp)){
        Text("Comparaison", fontSize = 22.sp, fontWeight = FontWeight.Bold)

        Spacer(modifier = Modifier.height(18.dp))

        // Chart Area (Placeholder for Graph)
        Box(modifier = Modifier.fillMaxSize()) {
            Text(
                "Graph will be here",
                modifier = Modifier.align(androidx.compose.ui.Alignment.Center)
            )
        }
    }
}


@Composable
@Preview
fun ComparaisonPreview(){
    ComparaisonScreen()
}