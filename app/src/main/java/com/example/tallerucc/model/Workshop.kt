package com.example.tallerucc.model

import com.google.firebase.firestore.DocumentReference

data class Category(
    val id: String = "", // ID único de la categoría
    val name: String = "", // Nombre de la categoría
    val reference: DocumentReference
)

data class Schedule(
    val day: String = "",
    val startTime: String,
    val endTime: String
)

data class Workshop(
    val id: String = "", // ID único del taller
    val name: String = "",
    val description: String = "",
    val categoryId: DocumentReference? = null, // Cambiado a DocumentReference
    val imageUrl: String = "", // URL de la imagen del taller (opcional)
    val schedule: List<Schedule> = emptyList() // Horarios del taller
)


