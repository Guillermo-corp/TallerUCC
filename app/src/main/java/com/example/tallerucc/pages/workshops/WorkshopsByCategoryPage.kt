package com.example.tallerucc.pages.workshops

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.tallerucc.R
import com.example.tallerucc.navigation.navItems
import com.example.tallerucc.pages.composables.BottomNavBar
import com.example.tallerucc.pages.composables.Header
import com.example.tallerucc.pages.composables.TimePickerButton
import com.example.tallerucc.pages.composables.WorkshopItem
import com.example.tallerucc.repository.WorkshopRepository
import com.example.tallerucc.ui.theme.DarkBlue
import com.example.tallerucc.ui.theme.LightBlue
import com.example.tallerucc.ui.theme.Typography
import com.example.tallerucc.viewModel.AuthViewModel
import com.example.tallerucc.viewModel.NavigationViewModel
import com.example.tallerucc.viewModel.NotificationViewModel
import com.example.tallerucc.viewModel.WorkshopViewModel
import com.example.tallerucc.viewModel.WorkshopViewModelFactory
import com.google.firebase.firestore.DocumentReference


@Composable
fun WorkshopsByCategoryPage(
    navController: NavController,
    categoryReference: DocumentReference,
    navigationViewModel: NavigationViewModel,
    modifier: Modifier = Modifier,
    authViewModel: AuthViewModel,
    notificationViewModel: NotificationViewModel,
) {
    val repository = WorkshopRepository()
    val viewModel: WorkshopViewModel = viewModel(factory = WorkshopViewModelFactory(repository))
    val workshops by viewModel.workshops.collectAsState()
    val selectedIndex by navigationViewModel.selectedIndex.collectAsState()

    // Estados para el filtro de horas
    var filterStartTime by remember { mutableStateOf<Long?>(null) }
    var filterEndTime by remember { mutableStateOf<Long?>(null) }

    // Constante para 1 hora y 30 minutos en milisegundos
    val MINIMUM_REQUIRED_TIME = 90 * 60 * 1000 // 90 minutos en milisegundos

// Filtrar talleres
    val filteredWorkshops = workshops.filter { workshop ->
        filterStartTime == null || filterEndTime == null || workshop.schedule.any { schedule ->
            val workshopStartTime = parseTimeToMillis(schedule.startTime)
            val workshopEndTime = parseTimeToMillis(schedule.endTime)

            // Cálculo del tiempo compartido entre los rangos
            val overlapStart = maxOf(filterStartTime!!, workshopStartTime)
            val overlapEnd = minOf(filterEndTime!!, workshopEndTime)

            // Duración de la intersección en milisegundos
            val overlapDuration = overlapEnd - overlapStart

            // Validar si la intersección cumple al menos 90 minutos
            overlapDuration >= MINIMUM_REQUIRED_TIME
        }
    }


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
                onItemSelected = { navigationViewModel.selectIndex(it) },
                unreadNotificationsCount = notificationViewModel.unreadNotificationsCount.collectAsState().value
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
            Text(
                text = "Inserta tu horario libre",
                style = Typography.titleSmall,
                modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 16.dp),
                color = DarkBlue
            )
            // Filtro de horas
            // Filtro de horas con Box para el efecto "wrapped"
            Box(
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 8.dp) // Margen externo
                    .border(
                        width = 2.dp,
                        color = LightBlue, // Color del borde
                        shape = RoundedCornerShape(100.dp) // Curvatura del borde
                    )
                    .padding(horizontal = 4.dp) // Espaciado interno dentro del borde
            ) {
                Row(
                    modifier = Modifier,
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    TimePickerButton(
                        text = "Inicio",
                        selectedTime = filterStartTime,
                        onTimeSelected = { filterStartTime = it },
                    )

                    Icon(
                        painter = painterResource(id = R.drawable.ic_time),
                        contentDescription = "Custom Drawable Icon",
                        modifier = Modifier.size(32.dp),
                        tint = LightBlue
                    )

                    TimePickerButton(
                        text = "Final",
                        selectedTime = filterEndTime,
                        onTimeSelected = { filterEndTime = it }
                    )
                }
            }


            HorizontalDivider(
                color = LightBlue.copy(alpha = 0.2f),
                thickness = 1.dp,
                modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 8.dp)
            )

            if (filteredWorkshops.isNotEmpty()) {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(0.dp),
                ) {
                    items(filteredWorkshops) { workshop ->
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

fun parseTimeToMillis(time: String): Long {
    val parts = time.split(":")
    val hours = parts[0].toIntOrNull() ?: 0
    val minutes = parts[1].toIntOrNull() ?: 0
    return (hours * 60 * 60 * 1000 + minutes * 60 * 1000).toLong()
}






