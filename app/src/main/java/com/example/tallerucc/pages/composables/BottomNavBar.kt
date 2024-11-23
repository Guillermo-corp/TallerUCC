package com.example.tallerucc.pages.composables

import androidx.compose.foundation.layout.height
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.tallerucc.navigation.NavItem
import com.example.tallerucc.ui.theme.Typography

@Composable
fun BottomNavBar(
    navController: NavController,
    navItems: List<NavItem>,
    selectedIndex: Int,
    onItemSelected: (Int) -> Unit
) {
    NavigationBar (
        modifier = Modifier
            .height(112.dp)
    ) {
        navItems.forEachIndexed { index, navItem ->
            NavigationBarItem(
                icon = {
                    BadgedBox(badge = {
                        if (navItem.badgeCount != null && navItem.badgeCount > 0) {
                            Badge {
                                Text(text = navItem.badgeCount.toString())
                            }
                        }
                    }) {
                        Icon(imageVector = navItem.icon, contentDescription = navItem.label)
                    }
                },
                label = { Text(
                    text = navItem.label,
                    maxLines = 1,
                    style = Typography.labelSmall
                ) },
                selected = selectedIndex == index,
                onClick = {
                    if (navController.currentDestination?.route != navItem.route) {
                        navController.navigate(navItem.route) {
                            launchSingleTop = true
                            restoreState = true
                        }
                        onItemSelected(index)
                    }
                }
            )
        }
    }
}