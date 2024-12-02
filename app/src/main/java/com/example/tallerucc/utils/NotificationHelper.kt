package com.example.tallerucc.utils

import android.Manifest
import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.tallerucc.MainActivity
import com.example.tallerucc.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

object NotificationHelper {

    fun showNotification(context: Context, title: String, body: String) {
        val channelId = "default_channel_id"
        val notificationId = System.currentTimeMillis().toInt()

        // Crear un Intent para abrir la app en NotificationsPage
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("navigate_to", "notifications") // Agrega un extra para la navegación
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE // Asegúrate de usar FLAG_IMMUTABLE para Android 12+
        )

        // Construir la notificación
        val notificationBuilder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.logo_ucc_neutral) // Replace with your app's icon
            .setContentTitle(title)
            .setContentText(body)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent) // Establece el PendingIntent
            .setAutoCancel(true) // Dismiss notification when clicked

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Crear el canal de notificación si es necesario (Android 8.0+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Default Channel",
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(channel)
        }

        notificationManager.notify(notificationId, notificationBuilder.build())
    }

    fun updateDeviceToken(token: String) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId != null) {
            val db = FirebaseFirestore.getInstance()
            db.collection("users").document(userId).get()
                .addOnSuccessListener { document ->
                    val existingTokens = document.get("deviceTokens") as? List<String> ?: emptyList()
                    if (!existingTokens.contains(token)) {
                        db.collection("users").document(userId)
                            .update("deviceTokens", FieldValue.arrayUnion(token))
                            .addOnSuccessListener {
                                Log.d("NotificationHelper", "Token actualizado para usuario $userId")
                            }
                            .addOnFailureListener { e ->
                                Log.e("NotificationHelper", "Error al actualizar token: ${e.message}")
                            }
                    } else {
                        Log.d("NotificationHelper", "Token ya existe para el usuario $userId.")
                    }
                }
                .addOnFailureListener { e ->
                    Log.e("NotificationHelper", "Error al verificar tokens existentes: ${e.message}")
                }
        } else {
            Log.e("NotificationHelper", "Usuario no autenticado. Token no actualizado.")
        }
    }

}



