package com.example.tallerucc.pages.composables

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.tallerucc.navigation.NavItem
import com.example.tallerucc.ui.theme.DarkBlue
import com.example.tallerucc.ui.theme.DarkGrey
import com.example.tallerucc.ui.theme.LightBlue
import com.example.tallerucc.ui.theme.LightGrey
import com.example.tallerucc.ui.theme.Typography
import com.example.tallerucc.ui.theme.White

@Composable
fun BottomNavBar(
    navController: NavController,
    navItems: List<NavItem>,
    selectedIndex: Int,
    onItemSelected: (Int) -> Unit,
    unreadNotificationsCount: Int? = null // Nuevo parámetro
) {
    NavigationBar(
        modifier = Modifier
            .height(112.dp)
            .drawBehind {
                // Draw bottom border
                val strokeWidth = 1.dp.toPx()
                val y = strokeWidth / 2
                drawLine(
                    color = LightGrey, // Border color
                    start = Offset(0f, y),
                    end = Offset(size.width, y),
                    strokeWidth = strokeWidth
                )
            },
        containerColor = White,
    ) {
        navItems.forEachIndexed { index, navItem ->
            NavigationBarItem(
                icon = {
                    BadgedBox(badge = {
                        // Lógica para mostrar el badge dinámicamente
                        if (navItem.route == "notifications" && unreadNotificationsCount != null && unreadNotificationsCount > 0) {
                            Badge(
                                containerColor = LightBlue // Fondo del badge
                            ) {
                                Text(
                                    text = unreadNotificationsCount.toString(),
                                    color = White // Texto del badge
                                )
                            }
                        } else if (navItem.badgeCount != null && navItem.badgeCount > 0) {
                            Badge(
                                containerColor = LightBlue // Fondo del badge
                            ) {
                                Text(
                                    text = navItem.badgeCount.toString(),
//                                    color = White // Texto del badge
                                )
                            }
                        }
                    }) {
                        Icon(
                            imageVector = navItem.icon,
                            contentDescription = navItem.label,
//                            tint = if (selectedIndex == index) White else White.copy(alpha = 0.5f) // Color de íconos (activo/inactivo)
                        )
                    }
                },
                label = {
                    Text(
                        text = navItem.label,
                        maxLines = 1,
                        style = Typography.labelSmall,
//                        color = if (selectedIndex == index) White else White.copy(alpha = 0.5f) // Color del texto (activo/inactivo)
                    )
                },
                selected = selectedIndex == index,
                onClick = {
                    if (navController.currentDestination?.route != navItem.route) {
                        navController.navigate(navItem.route) {
                            launchSingleTop = true
                            restoreState = true
                        }
                        onItemSelected(index)
                    }
                },
//                colors = NavigationBarItemDefaults.colors(
//                    selectedIconColor = White, // Color del ícono activo
//                    selectedTextColor = White, // Color del texto activo
//                    unselectedIconColor = White.copy(alpha = 0.5f), // Color del ícono inactivo
//                    unselectedTextColor = White.copy(alpha = 0.5f), // Color del texto inactivo
//                    indicatorColor = LightBlue.copy(alpha = 0.5f)
//                )
                colors = NavigationBarItemDefaults.colors(
                selectedIconColor = DarkBlue, // Color del ícono activo
                selectedTextColor = DarkBlue, // Color del texto activo
                unselectedIconColor = DarkBlue.copy(alpha = 0.6f), // Color del ícono inactivo
                unselectedTextColor = DarkBlue.copy(alpha = 0.5f), // Color del texto inactivo
                indicatorColor = LightBlue.copy(alpha = 0.2f)
            )
            )
        }
    }
}
