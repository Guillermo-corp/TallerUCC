package com.example.tallerucc.pages

import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.tallerucc.pages.composables.Header
import com.example.tallerucc.pages.composables.ImagePickerButton
import com.example.tallerucc.pages.composables.TimePickerButton
import com.example.tallerucc.pages.composables.formatTime
import com.example.tallerucc.pages.create.CommunityForm
import com.example.tallerucc.pages.create.CreationChips
import com.example.tallerucc.pages.create.CreationTabs
import com.example.tallerucc.pages.create.PostForm
import com.example.tallerucc.pages.create.WorkshopForm
import com.example.tallerucc.repository.uploadImageToImgur
import com.example.tallerucc.viewModel.AuthViewModel
import com.example.tallerucc.viewModel.CreateViewModel
import com.google.firebase.firestore.DocumentReference

@OptIn(ExperimentalMaterial3Api::class, androidx.compose.foundation.ExperimentalFoundationApi::class)
@Composable
fun CreatePage(
    navController: NavController,
    createViewModel: CreateViewModel,
    authViewModel: AuthViewModel
) {
    val userRoles by createViewModel.userRoles.collectAsState()

    var selectedOption by remember { mutableStateOf("Comunidad") }

    // Filtra las opciones en función de los roles
    val availableOptions = if ("admin" in userRoles) {
        listOf("Taller", "Comunidad", "Publicación")
    } else {
        listOf("Comunidad", "Publicación")
    }

    Scaffold(
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
        content = { innerPadding ->
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                CreationChips(
                    options = availableOptions,
                    selectedOption = selectedOption,
                    onOptionSelected = { selectedOption = it }
                )

                when (selectedOption) {
                    "Taller" -> WorkshopForm(navController, createViewModel)
                    "Comunidad" -> CommunityForm(navController, createViewModel)
                    "Publicación" -> PostForm(navController, createViewModel)
                }
            }
        }
    )
}




