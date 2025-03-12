package fr.uge.visualizer.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import fr.uge.visualizer.ui.theme.Blue
import fr.uge.visualizer.ui.theme.GreenButton

private val DarkColorScheme = darkColorScheme(
    primary = Blue,  // Utilisation de Blue
    secondary = GreenButton,  // Utilisation de GreenButton
    tertiary = Pink80
)

private val LightColorScheme = lightColorScheme(
    primary = Blue,
    secondary = GreenButton,
    tertiary = Pink40
)

@Composable
fun VisualizerTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
