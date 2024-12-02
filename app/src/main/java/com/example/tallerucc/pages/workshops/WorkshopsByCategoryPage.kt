package com.example.tallerucc.pages.workshops

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.tallerucc.navigation.navItems
import com.example.tallerucc.pages.composables.BottomNavBar
import com.example.tallerucc.pages.composables.Header
import com.example.tallerucc.pages.composables.WorkshopItem
import com.example.tallerucc.repository.WorkshopRepository
import com.example.tallerucc.ui.theme.Typography
import com.example.tallerucc.viewModel.AuthViewModel
import com.example.tallerucc.viewModel.NavigationViewModel
import com.example.tallerucc.viewModel.WorkshopViewModel
import com.example.tallerucc.viewModel.WorkshopViewModelFactory
import com.google.firebase.firestore.DocumentReference


@Composable
fun WorkshopsByCategoryPage(
    navController: NavController,
    categoryReference: DocumentReference,
    navigationViewModel: NavigationViewModel,
    modifier: Modifier = Modifier,
    authViewModel: AuthViewModel
) {
    val repository = WorkshopRepository()
    val viewModel: WorkshopViewModel = viewModel(factory = WorkshopViewModelFactory(repository))
    val workshops by viewModel.workshops.collectAsState()
    val selectedIndex by navigationViewModel.selectedIndex.collectAsState()

    LaunchedEffect(categoryReference) {
        viewModel.loadWorkshopsByCategory(categoryReference)
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
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(0.dp)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            if (workshops.isNotEmpty()) {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(0.dp),
                ) {
                    items(workshops) { workshop ->
                        WorkshopItem(
                            workshop = workshop,
                            onClick = {
                                navController.navigate("workshopDetail/${workshop.id}")
                            }
                        )
                    }
                }
            } else {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No hay talleres disponibles", style = Typography.bodyMedium)
                }
            }
        }
    }
}





