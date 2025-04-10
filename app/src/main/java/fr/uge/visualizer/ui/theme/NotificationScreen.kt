package fr.uge.visualizer.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fr.uge.visualizer.model.Notification
import fr.uge.visualizer.viewmodel.NotificationViewModel

@Composable
fun NotificationScreen(viewModel: NotificationViewModel, onNavigateBack: () -> Unit = {}) {
    val notifications by viewModel.notifications.collectAsState()
    val groupedNotifications = notifications.groupBy { it.group }

    Scaffold(
        topBar = {
            // En-tête
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF4F46E5))
                    .padding(16.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Retour",
                        tint = Color.White,
                        modifier = Modifier
                            .clickable { onNavigateBack() }
                            .padding(end = 8.dp)
                            .size(24.dp)
                    )
                    Text(
                        text = "Notifications",
                        color = Color.White,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    ) { paddingValues ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFFF3F4F6))
        ) {
            // Filtres
            item {
                ScrollableRow(
                    modifier = Modifier.padding(16.dp)
                ) {
                    FilterChip(
                        selected = true,
                        onClick = { },
                        label = { Text("Toutes") },
                        modifier = Modifier.padding(end = 8.dp)
                    )
                    FilterChip(
                        selected = false,
                        onClick = { },
                        label = { Text("Alertes") },
                        modifier = Modifier.padding(end = 8.dp)
                    )
                    FilterChip(
                        selected = false,
                        onClick = { },
                        label = { Text("Trafic") },
                        modifier = Modifier.padding(end = 8.dp)
                    )
                    FilterChip(
                        selected = false,
                        onClick = { },
                        label = { Text("Stations") },
                        modifier = Modifier.padding(end = 8.dp)
                    )
                }
            }

            // Notifications par groupe
            groupedNotifications.forEach { (group, groupNotifications) ->
                item {
                    Text(
                        text = group,
                        color = Color.Gray,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(start = 16.dp, top = 16.dp, bottom = 8.dp)
                    )
                }

                items(groupNotifications) { notification ->
                    NotificationCard(notification = notification)
                }
            }
        }
    }
}

@Composable
fun NotificationCard(notification: Notification) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            // Icône selon le type
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(
                        when (notification.type) {
                            "urgent" -> Color(0xFFFEE2E2)
                            "info" -> Color(0xFFEEF2FF)
                            else -> Color(0xFFECFDF5)
                        },
                        shape = RoundedCornerShape(8.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = when (notification.type) {
                        "urgent" -> Icons.Default.Warning
                        "info" -> Icons.Default.Info
                        else -> Icons.Default.Notifications
                    },
                    contentDescription = "Retour",
                    tint = when (notification.type) {
                        "urgent" -> Color(0xFFDC2626)
                        "info" -> Color(0xFF4F46E5)
                        else -> Color(0xFF059669)
                    }
                )
            }

            // Contenu
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 16.dp)
            ) {
                Text(
                    text = notification.title,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp
                )

                Text(
                    text = notification.message,
                    color = Color.Gray,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(top = 4.dp)
                )

                Text(
                    text = notification.time,
                    color = Color.LightGray,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }
    }
}

@Composable
fun ScrollableRow(
    modifier: Modifier = Modifier,
    content: @Composable RowScope.() -> Unit
) {
    Row(
        modifier = modifier.horizontalScroll(rememberScrollState()),
        content = content
    )
}