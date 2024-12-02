package com.example.tallerucc.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class NotificationViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()
    private val currentUser = FirebaseAuth.getInstance().currentUser

    private val _notifications = MutableStateFlow<List<Map<String, Any>>>(emptyList())
    val notifications: StateFlow<List<Map<String, Any>>> = _notifications

    private val _unreadNotificationsCount = MutableStateFlow(0)
    val unreadNotificationsCount: StateFlow<Int> = _unreadNotificationsCount

    init {
        loadNotifications()
        loadUnreadNotificationsCount()
    }

    // Cargar todas las notificaciones del usuario
    fun loadNotifications() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        db.collection("notifications")
            .whereEqualTo("userId", userId)
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e("NotificationViewModel", "Error loading notifications", error)
                    return@addSnapshotListener
                }

                _notifications.value = snapshot?.documents?.mapNotNull { document ->
                    document.data?.toMutableMap()?.apply {
                        this["id"] = document.id
                    }
                } ?: emptyList()
            }
    }


    // Cargar la cantidad de notificaciones no leídas
    fun loadUnreadNotificationsCount() {
        val userId = currentUser?.uid ?: return
        db.collection("notifications")
            .whereEqualTo("userId", userId)
            .whereEqualTo("read", false)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    println("Error loading unread notifications: ${error.message}")
                    return@addSnapshotListener
                }

                _unreadNotificationsCount.value = snapshot?.size() ?: 0
            }
    }

    // Marcar una notificación como leída
    fun markNotificationAsRead(notificationId: String) {
        db.collection("notifications").document(notificationId)
            .update("read", true)
            .addOnSuccessListener {
                println("Notification marked as read: $notificationId")
            }
            .addOnFailureListener { e ->
                println("Error marking notification as read: ${e.message}")
            }
    }

}


