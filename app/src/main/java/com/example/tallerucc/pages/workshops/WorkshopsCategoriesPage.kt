package com.example.tallerucc.pages.workshops

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.tallerucc.R
import com.example.tallerucc.navigation.navItems
import com.example.tallerucc.pages.composables.BottomNavBar
import com.example.tallerucc.pages.composables.CategoryItem
import com.example.tallerucc.pages.composables.FloatingActionButtonCustom
import com.example.tallerucc.pages.composables.Header
import com.example.tallerucc.pages.composables.categoryIcons
import com.example.tallerucc.repository.WorkshopRepository
import com.example.tallerucc.viewModel.AuthViewModel
import com.example.tallerucc.viewModel.NavigationViewModel
import com.example.tallerucc.viewModel.NotificationViewModel
import com.example.tallerucc.viewModel.WorkshopViewModel
import com.example.tallerucc.viewModel.WorkshopViewModelFactory

@Composable
fun WorkshopsCategoriesPage(
    navController: NavController,
    navigationViewModel: NavigationViewModel,
    modifier: Modifier = Modifier,
    authViewModel: AuthViewModel,
    notificationViewModel: NotificationViewModel,
) {
    val repository = WorkshopRepository()
    val viewModel: WorkshopViewModel = viewModel(factory = WorkshopViewModelFactory(repository))
    val categories by viewModel.categories.collectAsState()
    val selectedIndex by navigationViewModel.selectedIndex.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadCategories()
    }

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
        },
        floatingActionButton = {
            // FAB personalizado
            FloatingActionButtonCustom(
                onFabClick = {
                    navController.navigate("createPage")
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2), // 2 columnas
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(categories) { category ->
                    val iconRes = categoryIcons[category.name] ?: R.drawable.logo2_blanco_ch // Ícono predeterminado
                    CategoryItem(
                        categoryName = category.name,
                        iconRes = iconRes, // Pasar el recurso drawable
                        onClick = {
                            navController.navigate("workshops/${category.id}")
                        }
                    )
                }
            }
        }
    }
}

