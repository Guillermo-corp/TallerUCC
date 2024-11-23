package com.example.tallerucc.pages.workshops

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
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
import com.example.tallerucc.repository.WorkshopRepository
import com.example.tallerucc.ui.theme.Typography
import com.example.tallerucc.viewModel.NavigationViewModel
import com.example.tallerucc.viewModel.WorkshopViewModel
import com.example.tallerucc.viewModel.WorkshopViewModelFactory
import com.google.firebase.firestore.DocumentReference


@Composable
fun WorkshopDetailPage(
    navController: NavController,
    workshopReference: DocumentReference,
    navigationViewModel: NavigationViewModel,
    modifier: Modifier = Modifier
) {
    val repository = WorkshopRepository()
    val viewModel: WorkshopViewModel = viewModel(factory = WorkshopViewModelFactory(repository))
    val workshop by viewModel.workshopDetails.collectAsState()
    val selectedIndex by navigationViewModel.selectedIndex.collectAsState()

    LaunchedEffect(workshopReference) {
        viewModel.loadWorkshopDetails(workshopReference)
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            Header(title = workshop?.name ?: "Detalles del Taller")
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
                .padding(16.dp)
                .fillMaxSize(),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Top
        ) {
            if (workshop != null) {
                Text(workshop!!.name, style = Typography.titleLarge)
                Text(workshop!!.description, style = Typography.bodyMedium)
                Spacer(modifier = Modifier.height(16.dp))
                Text("Horario:", style = Typography.titleMedium)
                workshop!!.schedule.forEach { schedule ->
                    Text(
                        "${schedule.day}: ${schedule.startTime} - ${schedule.endTime}",
                        style = Typography.bodyMedium
                    )
                }
            } else {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
        }
    }
}

