package com.example.tallerucc.pages.workshops

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.tallerucc.R
import com.example.tallerucc.navigation.navItems
import com.example.tallerucc.pages.composables.BottomNavBar
import com.example.tallerucc.pages.composables.Header
import com.example.tallerucc.repository.WorkshopRepository
import com.example.tallerucc.ui.theme.Black
import com.example.tallerucc.ui.theme.DarkBlue
import com.example.tallerucc.ui.theme.DarkGrey
import com.example.tallerucc.ui.theme.LightBlue
import com.example.tallerucc.ui.theme.LightGrey
import com.example.tallerucc.ui.theme.Typography
import com.example.tallerucc.ui.theme.White
import com.example.tallerucc.viewModel.AuthViewModel
import com.example.tallerucc.viewModel.NavigationViewModel
import com.example.tallerucc.viewModel.NotificationViewModel
import com.example.tallerucc.viewModel.WorkshopViewModel
import com.example.tallerucc.viewModel.WorkshopViewModelFactory
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference


@Composable
fun WorkshopDetailPage(
    navController: NavController,
    workshopReference: DocumentReference,
    navigationViewModel: NavigationViewModel,
    modifier: Modifier = Modifier,
    authViewModel: AuthViewModel,
    notificationViewModel: NotificationViewModel,
) {
    val repository = WorkshopRepository()
    val viewModel: WorkshopViewModel = viewModel(factory = WorkshopViewModelFactory(repository))
    val workshop by viewModel.workshopDetails.collectAsState()
    val selectedIndex by navigationViewModel.selectedIndex.collectAsState()

    val workshopViewModel: WorkshopViewModel = viewModel(factory = WorkshopViewModelFactory(WorkshopRepository()))
    val context = LocalContext.current // Obtener el contexto


    LaunchedEffect(workshopReference) {
        viewModel.loadWorkshopDetails(workshopReference)
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
        if (workshop != null) {
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .padding(0.dp)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()), // Habilitar scroll para contenido largo
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Carrusel de imágenes
                if (workshop!!.imageUrls.isNotEmpty()) {
                    val pagerState = rememberPagerState(pageCount = { workshop!!.imageUrls.size })

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(330.dp)
                            .shadow(
                                elevation = 8.dp,
                                shape = RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp)
                            ) // Sombra para el carrusel
                            .clip(
                                RoundedCornerShape(
                                    bottomStart = 16.dp,
                                    bottomEnd = 16.dp
                                )
                            )
                    ) {
                        HorizontalPager(
                            state = pagerState,
                            modifier = Modifier
                                .fillMaxSize(),
                            pageSpacing = 8.dp
                        ) { page ->
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                            ) {
                                // Imagen
                                Image(
                                    painter = rememberAsyncImagePainter(workshop!!.imageUrls[page]),
                                    contentDescription = "Imagen del workshop",
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .clip(
                                            RoundedCornerShape(
                                                bottomStart = 16.dp,
                                                bottomEnd = 16.dp
                                            )
                                        ),
                                    contentScale = ContentScale.Crop
                                )

                                // Degradado en la parte inferior para resaltar puntos
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(100.dp)
                                        .align(Alignment.BottomCenter)
                                        .background(
                                            brush = Brush.verticalGradient(
                                                colors = listOf(
                                                    Color.Transparent,
                                                    Black.copy(alpha = 0.5f) // Gradiente hacia negro translúcido
                                                )
                                            )
                                        )
                                )
                            }
                        }

                        // Indicadores de página
                        Row(
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            repeat(workshop!!.imageUrls.size) { index ->
                                val isSelected = pagerState.currentPage == index
                                Box(
                                    modifier = Modifier
                                        .padding(horizontal = 4.dp)
                                        .size(if (isSelected) 12.dp else 8.dp)
                                        .clip(CircleShape)
                                        .background(
                                            if (isSelected) MaterialTheme.colorScheme.primary
                                            else LightGrey.copy(alpha = 0.6f)
                                        )
                                )
                            }
                        }
                    }
                } else {
                    Text(
                        text = "Sin imágenes disponibles",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                }


                // Nombre del workshop
                Text(
                    text = workshop!!.name,
                    style = Typography.titleLarge,
                    modifier = Modifier.padding(horizontal = 16.dp),
                    color = DarkBlue
                )

                // Descripción del workshop
                Text(
                    text = workshop!!.description,
                    style = Typography.bodyLarge,
                    modifier = Modifier.padding(horizontal = 16.dp),
                    color = DarkGrey
                )

                HorizontalDivider(
                    modifier = Modifier
                        .padding(8.dp),
                    thickness = 1.dp,
                    color = LightBlue.copy(alpha = 0.2f)
                )

//                // Horarios del workshop
//                Text(
//                    text = "Horario:",
//                    style = Typography.titleMedium,
//                    modifier = Modifier.padding(horizontal = 16.dp),
//                    color = DarkBlue
//                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 72.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Ícono alineado a la izquierda
                    Icon(
                        imageVector = Icons.Default.DateRange,
                        contentDescription = "Info Icon",
                        modifier = Modifier.size(32.dp),
                        tint = LightBlue
                    )

                    Icon(
                        painter = painterResource(id = R.drawable.ic_time),
                        contentDescription = "Custom Drawable Icon",
                        modifier = Modifier.size(32.dp),
                        tint = LightBlue
                    )
                }


                workshop!!.schedule.forEach { schedule ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 40.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        // Día alineado a la izquierda
                        Text(
                            text = schedule.day,
                            style = Typography.titleSmall,
                            color = LightBlue
                        )

                        // Hora alineada a la derecha
                        Text(
                            text = "${schedule.startTime} - ${schedule.endTime}",
                            style = Typography.titleSmall,
                            color = DarkGrey
                        )
                    }
                }

                Button(
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    onClick = {
                        val currentUserEmail = FirebaseAuth.getInstance().currentUser?.email
                        val workshopName = workshop?.name // Asegúrate de tener acceso al nombre del workshop

                        if (currentUserEmail != null && workshopName != null) {
                            workshopViewModel.joinCommunityByWorkshop(
                                workshopName = workshopName,
                                userEmail = currentUserEmail,
                                onSuccess = {
                                    Toast.makeText(context, "Te has unido a la comunidad.", Toast.LENGTH_SHORT).show()
                                },
                                onFailure = { error ->
                                    Toast.makeText(context, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
                                }
                            )
                        }
                    }
                ) {
                    Text(
                        text = "Unirse",
                        style = Typography.titleMedium,
                        color = White
                    )
                }




            }
        } else {
            // Mostrar un indicador de carga mientras se obtienen los detalles
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
    }
}


