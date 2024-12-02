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
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.tallerucc.ui.theme.DarkBlue
import com.example.tallerucc.ui.theme.LightBlue
import com.example.tallerucc.ui.theme.LightGrey
import com.example.tallerucc.ui.theme.Typography
import com.example.tallerucc.ui.theme.White


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
                color = DarkBlue
            )
        },
        navigationIcon = {
            if (showBackIcon) {
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack, // Flecha hacia atrás
                        contentDescription = "Back Icon",
                        tint = LightBlue
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
                        tint = LightBlue
                    )
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.Transparent,
            titleContentColor = DarkBlue
        ),
        modifier = Modifier
            .drawBehind {
                // Draw bottom border
                val strokeWidth = 1.dp.toPx()
                val y = size.height - strokeWidth / 2
                drawLine(
                    color = LightGrey, // Color of the border
                    start = Offset(0f, y),
                    end = Offset(size.width, y),
                    strokeWidth = strokeWidth
                )
            }
    )
}



