package com.example.tallerucc.pages.create

import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.tallerucc.pages.composables.CommunitySearchBar
import com.example.tallerucc.pages.composables.ImagePickerButton
import com.example.tallerucc.pages.composables.ImagePickerPreview
import com.example.tallerucc.repository.uploadImageToImgur
import com.example.tallerucc.ui.theme.DarkBlue
import com.example.tallerucc.ui.theme.Typography
import com.example.tallerucc.viewModel.CreateViewModel
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostForm(navController: NavController, createViewModel: CreateViewModel) {
    // Estados del formulario
    var title by remember { mutableStateOf("") }
    var textContent by remember { mutableStateOf("") }
    val selectedImageUris = remember { mutableStateListOf<Uri>() }
    val uploadedImageUrls = remember { mutableStateListOf<String>() }
    val uploadedImagesSet = remember { mutableStateListOf<Uri>() }
    var selectedCommunityId by remember { mutableStateOf<String?>(null) } // ID de la comunidad seleccionada
    var selectedCommunity by remember { mutableStateOf<String?>(null) }
    var selectedCommunityLogo by remember { mutableStateOf<String?>(null) } // Logo de la comunidad seleccionada
    var isOfficial by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    val context = LocalContext.current

    // Obtener los roles del usuario
    val userRoles by createViewModel.userRoles.collectAsState()

    // Comunidades que sigue el usuario
    val followedCommunities by createViewModel.followedCommunities.collectAsState()

    // Filtrar comunidades según la búsqueda
    val filteredCommunities = followedCommunities.filter {
        it.name.contains(searchQuery, ignoreCase = true)
    }


    Text(
        text = "Publicar en",
        style = Typography.titleMedium,
        color = DarkBlue,
        modifier = Modifier.padding(horizontal = 16.dp)
    )

    if ("admin" in userRoles){
        // Checkbox para marcar como publicación oficial
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .fillMaxWidth(),
        ) {
            Checkbox(
                checked = isOfficial,
                onCheckedChange = { isOfficial = it }
            )
            Text(text = "Publicación oficial")
        }
    }

    if (!isOfficial) {
        CommunitySearchBar(
            followedCommunities = createViewModel.followedCommunities.collectAsState().value,
            onCommunitySelected = { community ->
                selectedCommunity = community.name // Nombre de la comunidad
                selectedCommunityLogo = community.iconUrl // Logo de la comunidad
                selectedCommunityId = community.id // ID de la comunidad
            },
        )
    }

    // Campo de título
    OutlinedTextField(
        value = title,
        onValueChange = { title = it },
        label = { Text("Título") },
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    )

    // Campo de contenido
    OutlinedTextField(
        value = textContent,
        onValueChange = { textContent = it },
        label = { Text("Contenido") },
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        minLines = 3,
        maxLines = 5
    )


    // Selector de imágenes
    ImagePickerButton { uri ->
        if (uri != null && !selectedImageUris.contains(uri)) {
            selectedImageUris.add(uri)
        }
    }

    if (selectedImageUris.isNotEmpty()) {
        val pagerState = rememberPagerState(pageCount = { selectedImageUris.size })

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Carrusel
            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp)
                    .height(160.dp), // Altura ajustada para que sea compacto
                pageSpacing = 16.dp, // Espaciado entre páginas
                pageSize = PageSize.Fixed(160.dp),
                beyondViewportPageCount = 2, // Mostrar parcialmente las imágenes adyacentes
                verticalAlignment = Alignment.CenterVertically // Centrar verticalmente las imágenes
            ) { page ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(MaterialTheme.shapes.medium) // Bordes redondeados
                ) {
                    Image(
                        painter = rememberAsyncImagePainter(selectedImageUris[page]),
                        contentDescription = "Imagen seleccionada",
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(MaterialTheme.shapes.medium), // Bordes redondeados
                        contentScale = ContentScale.Crop // Asegurar que la imagen llene el espacio
                    )
                }
            }

            // Indicador de posición actual (propiedad del PagerState)
            Text(
                text = "${pagerState.currentPage + 1} / ${selectedImageUris.size}",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }

    // Botón para subir imágenes
    Button(
        onClick = {
            val imagesToUpload = selectedImageUris.filter { it !in uploadedImagesSet }
            var successfullyUploadedCount = 0

            if (imagesToUpload.isNotEmpty()) {
                imagesToUpload.forEach { uri ->
                    createViewModel.uploadImage(uri, context) { imageUrl ->
                        if (imageUrl != null) {
                            uploadedImageUrls.add(imageUrl)
                            uploadedImagesSet.add(uri)
                            successfullyUploadedCount++

                            if (successfullyUploadedCount == imagesToUpload.size) {
                                Toast.makeText(
                                    context,
                                    "Todas las imágenes se subieron correctamente.",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        } else {
                            Toast.makeText(
                                context,
                                "Error al subir una imagen. Intenta nuevamente.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            } else {
                Toast.makeText(
                    context,
                    "No hay imágenes nuevas para subir.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        },
        enabled = selectedImageUris.isNotEmpty(),
        modifier = Modifier
            .padding(horizontal = 16.dp)
    ) {
        Text(
            text = "Confirmar Imágenes",
            style = Typography.titleSmall
        )
    }


    // Botón para crear publicación
    Button(
        onClick = {
            if (title.isNotEmpty() && textContent.isNotEmpty()) {
                if (uploadedImageUrls.isNotEmpty()) {
                    // Crear la publicación directamente
                    createViewModel.createPost(
                        context = context,
                        title = title,
                        textContent = textContent,
                        authorId = FirebaseAuth.getInstance().currentUser?.email ?: "",
                        communityId = if (isOfficial) null else selectedCommunityId, // Pasar communityId
                        communityName = if (isOfficial) "Universidad Cristóbal Colón" else selectedCommunity,
                        communityLogo = if (isOfficial) "https://i.imgur.com/DCzUFzG.png" else selectedCommunityLogo,
                        imageUrls = uploadedImageUrls,
                        isOfficial = isOfficial,
                        onSuccess = {
                            Toast.makeText(
                                context,
                                "Publicación creada exitosamente.",
                                Toast.LENGTH_SHORT
                            ).show()
                            navController.popBackStack()
                        },
                        onFailure = { error ->
                            Toast.makeText(
                                context,
                                "Error: ${error.message}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    )
                } else {
                    Toast.makeText(
                        context,
                        "Por favor, confirma las imágenes antes de crear la publicación.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } else {
                Toast.makeText(
                    context,
                    "Completa todos los campos obligatorios.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        },
        enabled = uploadedImageUrls.isNotEmpty(),
        modifier = Modifier
            .padding(start = 16.dp, end = 16.dp, bottom = 48.dp)
            .fillMaxWidth(),
        shape = RoundedCornerShape(8.dp)
    ) {
        Text(text = "Crear Publicación")
    }



}


