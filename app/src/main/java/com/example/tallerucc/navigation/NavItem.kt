package com.example.tallerucc.navigation

import androidx.compose.ui.graphics.vector.ImageVector

data class NavItem(
    val label: String,
    val icon: ImageVector,
    val route: String,
    val badgeCount: Int? = null
)
