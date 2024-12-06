package com.example.tallerucc.pages

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.tallerucc.navigation.navItems
import com.example.tallerucc.pages.composables.BottomNavBar
import com.example.tallerucc.pages.composables.Header
import com.example.tallerucc.pages.composables.NotificationCard
import com.example.tallerucc.ui.theme.Typography
import com.example.tallerucc.viewModel.AuthState
import com.example.tallerucc.viewModel.AuthViewModel
import com.example.tallerucc.viewModel.CommunityViewModel
import com.example.tallerucc.viewModel.NavigationViewModel
import com.example.tallerucc.viewModel.NotificationViewModel
import com.google.firebase.Timestamp

@Composable
fun NotificationPage(
    modifier: Modifier = Modifier,
    navController: NavController,
    notificationViewModel: NotificationViewModel,
    navigationViewModel: NavigationViewModel,
    authViewModel: AuthViewModel,
    communityViewModel: CommunityViewModel // Añadir este parámetro
) {
    val selectedIndex by navigationViewModel.selectedIndex.collectAsState()
    val notifications by notificationViewModel.notifications.collectAsState()
    val communities by communityViewModel.communities.collectAsState() // Observa comunidades

    Scaffold(
        modifier = modifier,
        topBar = {
            Header(
                title = "Tu Taller UCC",
                showBackIcon = true,
                onBackClick = { navController.popBackStack() }, // Navegar hacia atrás
                showLogoutIcon = true,
                onLogoutClick = {
                    authViewModel.signout() // Cerrar sesión
                    navController.navigate("login") { // Redirigir a la pantalla de inicio de sesión
                        popUpTo(0) // Limpia la pila de navegación
                    }
                }
            )
        },
        bottomBar = {
            BottomNavBar(
                navController = navController,
                navItems = navItems,
                selectedIndex = selectedIndex,
                onItemSelected = { navigationViewModel.selectIndex(it) },
                unreadNotificationsCount = notificationViewModel.unreadNotificationsCount.collectAsState().value
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (notifications.isEmpty()) {
                Text(
                    text = "No tienes notificaciones",
                    style = Typography.bodyMedium,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            } else {
                LazyColumn {
                    items(notifications) { notification ->
                        val communityId = notification["communityId"] as? String // Puede ser null

                        val isOfficial = communityId?.let { id ->
                            communities.find { it.id == id }?.isOfficial == true
                        } ?: false

                        NotificationCard(
                            title = notification["title"] as String,
                            message = notification["message"] as String,
                            timestamp = notification["timestamp"] as Timestamp,
                            isRead = notification["read"] as Boolean,
                            communityLogo = notification["imageUrl"] as? String,
                            communityId = notification["communityId"] as? String, // Nuevo campo
                            isOfficial = isOfficial, // Pasar el estado de oficial
                            onClick = {
                                notificationViewModel.markNotificationAsRead(notification["id"] as String)
                                if (communityId != null) {
                                    navController.navigate("communityDetail/$communityId")
                                } else {
                                    // No navegar, solo marcar como leída
                                    Log.d("NotificationPage", "Notification marked as read, no navigation required.")
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}


