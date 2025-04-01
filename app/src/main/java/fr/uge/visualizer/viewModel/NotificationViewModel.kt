// Dans viewmodel/NotificationViewModel.kt
package fr.uge.visualizer.viewmodel

import androidx.lifecycle.ViewModel
import fr.uge.visualizer.model.Notification
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class NotificationViewModel : ViewModel() {

    private val _notifications = MutableStateFlow<List<Notification>>(
        listOf(
            Notification(
                id = "1",
                title = "Trafic dense à Châtelet",
                message = "Affluence importante prévue entre 17h et 19h.",
                time = "Il y a 5 minutes",
                type = "urgent",
                group = "Aujourd'hui"
            ),
            Notification(
                id = "2",
                title = "Mise à jour des prévisions",
                message = "Nouvelles données disponibles pour votre trajet habituel.",
                time = "Il y a 2 heures",
                type = "info",
                group = "Aujourd'hui"
            ),
            Notification(
                id = "3",
                title = "Trafic fluide",
                message = "Le trafic est redevenu normal sur votre ligne.",
                time = "Hier à 18:30",
                type = "success",
                group = "Hier"
            )
        )
    )
    val notifications: StateFlow<List<Notification>> = _notifications

    val selectedFilter = MutableStateFlow("Toutes")

    fun setFilter(filter: String) {
        selectedFilter.value = filter
        // Dans une vraie app, on filtrerait les notifications selon le filtre
    }
}