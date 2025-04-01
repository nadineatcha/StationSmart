package fr.uge.visualizer.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import fr.uge.visualizer.model.Notification
import fr.uge.visualizer.repository.StationRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class NotificationViewModel : ViewModel() {
    private val repository = StationRepository()

    // Types de filtre possibles
    enum class NotificationFilter {
        ALL, ALERTS, TRAFFIC, STATIONS
    }

    // État de chargement
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    // Liste de toutes les notifications
    private val _allNotifications = MutableStateFlow<List<Notification>>(emptyList())

    // Liste filtrée de notifications
    private val _notifications = MutableStateFlow<List<Notification>>(emptyList())
    val notifications: StateFlow<List<Notification>> = _notifications

    // Filtre actuellement sélectionné
    private val _selectedFilter = MutableStateFlow(NotificationFilter.ALL)
    val selectedFilter: StateFlow<NotificationFilter> = _selectedFilter

    init {
        // Charger les notifications au démarrage
        loadNotifications()
    }

    // Fonction pour définir le filtre
    fun setFilter(filter: NotificationFilter) {
        _selectedFilter.value = filter
        updateFilteredNotifications()
    }

    // Fonction pour filtrer par chaîne de caractère (pour la compatibilité avec le code existant)
    fun setFilter(filter: String) {
        _selectedFilter.value = when(filter) {
            "Alertes" -> NotificationFilter.ALERTS
            "Trafic" -> NotificationFilter.TRAFFIC
            "Stations" -> NotificationFilter.STATIONS
            else -> NotificationFilter.ALL
        }
        updateFilteredNotifications()
    }

    // Mettre à jour les notifications filtrées en fonction du filtre sélectionné
    private fun updateFilteredNotifications() {
        _notifications.value = when (_selectedFilter.value) {
            NotificationFilter.ALL -> _allNotifications.value
            NotificationFilter.ALERTS -> _allNotifications.value.filter { it.type == "urgent" }
            NotificationFilter.TRAFFIC -> _allNotifications.value.filter { it.category == "traffic" }
            NotificationFilter.STATIONS -> _allNotifications.value.filter { it.category == "stations" }
        }
    }

    // Fonction pour charger les notifications depuis l'API
    fun loadNotifications() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // Charger les notifications depuis l'API
                val notifications = repository.getNotifications()
                _allNotifications.value = notifications
                updateFilteredNotifications()

                Log.d("NOTIFICATION_VM", "Notifications chargées: ${notifications.size}")
            } catch (e: Exception) {
                Log.e("NOTIFICATION_VM", "Erreur lors du chargement des notifications", e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Fonction pour rafraîchir les notifications
    fun refreshNotifications() {
        loadNotifications()
    }
}