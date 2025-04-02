
package fr.uge.test.ui.theme

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun TermsScreen(onNextClicked: () -> Unit) {
    val scrollState = rememberScrollState()
    val isChecked = remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .verticalScroll(scrollState),
    ) {
        // Titre "Legal" géré par la barre de navigation dans l'image

        // Contenu des termes dans une carte blanche
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
                .padding(bottom = 16.dp)
        ) {
            // Section termes et conditions
            Text(
                text = "Termes et règles d'utilisation",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(start = 8.dp, bottom = 8.dp)
            )

            Text(
                text = "Lorem ipsum dolor sit amet consectetur.\n" +
                        "Bibendum quis ipsum ut neque mattis feugiat facilisi. Uma cursus consectetur aliquet sit tristique tincidunt lacinia. Sapien dis eget egestas tellus sit dictumst dignissim. Sed suspendisse dignissim mattis ut lectus aliquam. Tortor ut aenean turpis odio eleifend elementum. Rhoncus vel integer aliquet ante enim interdum scelerisque sed quis. Possuere eget placerat amet sollicitudin venenatis quam eu mattis purus. Dictum quis in tellus diam possuere lectus mi. Id mauris et ultrices fringilla urna egestas enim.",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Justify,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 8.dp)
            )

            Text(
                text = "Lorem ipsum dolor sit amet consectetur.\n" +
                        "Bibendum quis ipsum ut neque mattis feugiat facilisi. Uma cursus consectetur aliquet sit tristique tincidunt lacinia. Sapien dis eget egestas tellus sit",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Justify,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 8.dp)
            )
        }

        // Checkbox avec texte
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp, bottom = 16.dp)
        ) {
            Checkbox(
                checked = isChecked.value,
                onCheckedChange = { isChecked.value = it }
            )

            Text(
                text = "En cochant cette case, je reconnais avoir pris connaissance des conditions générales et les accepter",
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(start = 4.dp)
            )
        }

        // Bouton Suivant
        Button(
            onClick = onNextClicked,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp, bottom = 16.dp),
            enabled = isChecked.value,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF2962FF),
                disabledContainerColor = Color(0xFF2962FF).copy(alpha = 0.5f)
            ),
            shape = RoundedCornerShape(24.dp)
        ) {
            Text(
                text = "Suivant",
                color = Color.White,
                fontSize = 16.sp,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }
    }
}