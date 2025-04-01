package fr.uge.test.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
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
import fr.uge.test.ui.theme.Blue
import fr.uge.test.ui.theme.GreenButton
import fr.uge.test.ui.theme.MainScreen


@Composable
fun SplashScreen(navController: NavController) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Blue),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            Spacer(modifier = Modifier.weight(1f))

            // Icône du train et texte
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "🚆", // Symbole de train que vous pouvez copier-coller
                    fontSize = 32.sp,
                    color = Color(0xFF4AEEB2)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Visualizer",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF4AEEB2)
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            // Bouton Commencer
            Button(
                onClick = {
                    // Navigation vers l'écran suivant
                    navController.navigate("Terms")
                },
                colors = ButtonDefaults.buttonColors(GreenButton),
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier.padding(bottom = 48.dp)
            ) {
                Text(
                    text = "Commencer",
                    color = Color.White,
                    fontSize = 16.sp,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
                Text(
                    text = "→",
                    color = Color.White,
                    fontSize = 16.sp
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SplashScreenPreview() {
    SplashScreen(rememberNavController())
}