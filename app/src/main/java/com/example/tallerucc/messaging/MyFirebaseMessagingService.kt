package com.example.tallerucc.messaging

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.tallerucc.R
import com.example.tallerucc.utils.NotificationHelper
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import com.example.tallerucc.utils.NotificationHelper.updateDeviceToken
import com.google.firebase.firestore.FieldValue

class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d("FCM", "New token: $token")

        saveTokenToFirestore(token)

        // Actualizar el token si el usuario está autenticado
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId != null) {
            updateDeviceToken(token)
        } else {
            Log.e("FCM", "Usuario no autenticado. Token no se actualizó.")
            // Si necesitas manejar tokens para usuarios no autenticados, guarda aquí
        }
    }


    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        val title = remoteMessage.notification?.title ?: ""
        val body = remoteMessage.notification?.body ?: ""

        // Display notification using NotificationHelper
        NotificationHelper.showNotification(this, title, body)
    }

    private fun saveTokenToFirestore(token: String) {
        val db = FirebaseFirestore.getInstance()
        val userId = FirebaseAuth.getInstance().currentUser?.uid // Obtiene el userId dinámicamente
        if (userId != null) {
            db.collection("users").document(userId)
                .set(mapOf("deviceToken" to token), com.google.firebase.firestore.SetOptions.merge())
                .addOnSuccessListener {
                    Log.d("FCM", "Token saved to Firestore")
                }
                .addOnFailureListener { e ->
                    Log.e("FCM", "Failed to save token", e)
                }
        } else {
            Log.e("FCM", "User is not authenticated, cannot save token.")
        }
    }

}


