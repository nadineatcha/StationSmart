package fr.uge.visualizer.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color



// Définissez votre palette de couleurs
private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF3F51B5), // Couleur principale
    secondary = Color(0xFFE91E63), // Couleur secondaire
    background = Color(0xFFFFFFFF), // Couleur de fond
    surface = Color(0xFFFFFFFF), // Couleur de surface
    onPrimary = Color(0xFFFFFFFF), // Couleur du texte sur la couleur principale
    onSecondary = Color(0xFFFFFFFF), // Couleur du texte sur la couleur secondaire
    onBackground = Color(0xFF000000), // Couleur du texte sur le fond
    onSurface = Color(0xFF000000) // Couleur du texte sur la surface
)

// Définissez votre thème
@Composable
fun MonDetailsTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = LightColorScheme, // Utilisez votre palette de couleurs


        content = content
    )
}