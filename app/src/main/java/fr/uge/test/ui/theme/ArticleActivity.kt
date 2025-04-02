package fr.uge.test.ui.theme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Warning
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
import androidx.navigation.compose.rememberNavController


@Composable
fun ArticlesScreen(navController: NavController) {
    Scaffold(
        topBar = {
            // Custom Top Bar for Articles
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF4285F4))
                    .padding(16.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White,
                        modifier = Modifier
                            .clickable { navController.popBackStack() }.clickable { navController.navigate("settings")}
                            .padding(end = 8.dp)
                            .size(24.dp)
                    )
                    Text(
                        text = "Articles",
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    ) { paddingValues ->
        ArticlesContent(Modifier.padding(paddingValues), navController)
    }
}

@Composable
fun ArticlesContent(modifier: Modifier = Modifier, navController: NavController) {
    Column(modifier = modifier.padding(16.dp)) {
        AlertCard(navController)
    }
}

@Composable
fun AlertCard(navController: NavController) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Alert Icon
            Icon(
                imageVector = Icons.Filled.Warning,
                contentDescription = "Traffic Alert",
                tint = Color.White,
                modifier = Modifier
                    .size(40.dp)
                    .background(Color(0xFFFFC107), shape = CircleShape)
                    .padding(8.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(
                modifier = Modifier.weight(1f) // Allows chevron to align properly
            ) {
                Text(
                    text = "TRAFFIC ALERT",
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Text(
                    text = "Traveaux - Lundi 17 février",
                    color = Color.Gray
                )
                Text(
                    text = "Toutes les infos dont vous avez besoin",
                    color = Color.Blue
                )
            }
            // Chevron Icon
            Icon(
                imageVector = Icons.Filled.ArrowForward, // Adding gray chevron icon
                contentDescription = "Chevron",
                tint = Color.Gray,
                modifier = Modifier
                    .size(24.dp)
                    .clickable { navController.navigate("Trafic Alert") }
            )
        }
    }
}