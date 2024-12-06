package com.example.tallerucc.utils

import com.google.firebase.Timestamp
import java.text.SimpleDateFormat
import java.util.*

/**
 * Convierte un Timestamp de Firebase a un formato legible.
 *
 * @param timestamp El Timestamp de Firebase.
 * @return Una cadena formateada con la fecha y hora, o "Fecha desconocida" si el timestamp es nulo.
 */
fun formatTimestamp(timestamp: Timestamp?): String {
    return if (timestamp != null) {
        val sdf = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault())
        sdf.format(timestamp.toDate()) // Convertir a Date y luego formatear
    } else {
        "Fecha desconocida"
    }
}


