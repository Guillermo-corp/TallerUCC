package com.example.tallerucc.pages.composables

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
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
    showProfileIcon: Boolean = true,
    onMenuClick: () -> Unit = {}, // Acciones para el menú, si las necesitas
    onProfileClick: () -> Unit = {}
) {
    TopAppBar(
        title = {
            Text(
                text = title,
                style = Typography.titleLarge,
                color = MaterialTheme.colorScheme.onPrimary
            )
        },
//        navigationIcon = {
//            IconButton(onClick = onMenuClick) {
//                Icon(
//                    imageVector = Icons.Default.Menu,
//                    contentDescription = "Menu Icon",
//                    tint = MaterialTheme.colorScheme.onPrimary
//                )
//            }
//        },
        actions = {
            if (showProfileIcon) {
                IconButton(onClick = onProfileClick) {
                    Icon(
                        imageVector = Icons.Default.AccountCircle,
                        contentDescription = "Profile Icon",
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


