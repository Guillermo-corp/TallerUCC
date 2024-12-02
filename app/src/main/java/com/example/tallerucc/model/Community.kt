package com.example.tallerucc.model

data class Community(
    val id: String, // ID del documento en Firestore
    val name: String, // Nombre de la comunidad
    val description: String, // Descripción de la comunidad
    val iconUrl: String?, // URL del ícono de la comunidad
    val bannerUrl: String?, // URL del banner de la comunidad
    val tags: List<String>, // Lista de etiquetas o tags de la comunidad
    val postsCount: Int = 0, // Número de publicaciones en la comunidad
    val createdAt: Long? = null, // Fecha de creación en formato timestamp
    val members: List<String> = emptyList(), // Lista de miembros
    val membersCount: Int = 0, // Cantidad de miembros
    val createdBy: String? = null, // Creador de la comunidad
    val isOfficial: Boolean = false // Indica si la comunidad es oficial
)

data class CommunityBasic(
    val name: String,
    val iconUrl: String
)



