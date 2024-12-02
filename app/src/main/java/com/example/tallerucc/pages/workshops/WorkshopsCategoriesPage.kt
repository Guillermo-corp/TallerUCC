package com.example.tallerucc.pages.workshops

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.tallerucc.viewModel.NavigationViewModel
import com.example.tallerucc.viewModel.WorkshopViewModel
import androidx.navigation.NavController
import com.example.tallerucc.navigation.navItems
import com.example.tallerucc.pages.composables.BottomNavBar
import com.example.tallerucc.pages.composables.Header
import com.example.tallerucc.pages.composables.CategoryItem
import com.example.tallerucc.pages.composables.FloatingActionButtonCustom
import com.example.tallerucc.repository.WorkshopRepository
import com.example.tallerucc.viewModel.AuthViewModel
import com.example.tallerucc.viewModel.WorkshopViewModelFactory


@Composable
fun WorkshopsCategoriesPage(
    navController: NavController,
    navigationViewModel: NavigationViewModel,
    modifier: Modifier = Modifier,
    authViewModel: AuthViewModel
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
                onItemSelected = { navigationViewModel.selectIndex(it) }
            )
        },
        floatingActionButton = {
            //FAB personalizado
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
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(categories) { category ->
                    CategoryItem(
                        categoryName = category.name,
                        onClick = {
                            navController.navigate("workshops/${category.id}")
                        }
                    )
                }
            }
        }
    }
}
