package com.example.tallerucc.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Star

val navItems = listOf(
    NavItem("Inicio", Icons.Default.Home, "home"),
    NavItem("Comunidades", Icons.Default.Favorite, "communities"),
    NavItem("Talleres", Icons.Default.Star, "workshops"),
    NavItem("Notificaciones", Icons.Default.Notifications, "notifications")
)


