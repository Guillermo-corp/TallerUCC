package com.example.tallerucc.pages.composables

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.tallerucc.ui.theme.Typography


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Header(
    title: String,
    showBackIcon: Boolean = false, // Mostrar flecha hacia atrás
    onBackClick: () -> Unit = {}, // Acción para la flecha
    showLogoutIcon: Boolean = false, // Mostrar ícono de cerrar sesión
    onLogoutClick: () -> Unit = {}, // Acción para cerrar sesión
) {
    TopAppBar(
        title = {
            Text(
                text = title,
                style = Typography.titleLarge,
                color = MaterialTheme.colorScheme.onPrimary
            )
        },
        navigationIcon = {
            if (showBackIcon) {
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack, // Flecha hacia atrás
                        contentDescription = "Back Icon",
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }
        },
        actions = {
            if (showLogoutIcon) {
                IconButton(onClick = onLogoutClick) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ExitToApp, // Ícono de logout
                        contentDescription = "Logout Icon",
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary,
            titleContentColor = Color.White
        ),
        modifier = Modifier
    )
}



