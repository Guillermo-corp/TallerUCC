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
import com.example.tallerucc.pages.create.CreationChips
import com.example.tallerucc.pages.create.CreationTabs
import com.example.tallerucc.pages.create.WorkshopForm
import com.example.tallerucc.repository.uploadImageToImgur
import com.example.tallerucc.viewModel.CreateViewModel
import com.google.firebase.firestore.DocumentReference

@OptIn(ExperimentalMaterial3Api::class, androidx.compose.foundation.ExperimentalFoundationApi::class)
@Composable
fun CreatePage(navController: NavController, createViewModel: CreateViewModel) {

    var selectedTab by remember { mutableStateOf(0) } // 0 = Workshop, 1 = Comunidad, etc.
    var selectedOption by remember { mutableStateOf("Workshop") }

    // Estados del formulario
    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    val selectedImageUris = remember { mutableStateListOf<Uri>() }
    val uploadedImageUrls = remember { mutableStateListOf<String>() }
    val uploadedImagesSet = remember { mutableStateListOf<Uri>() } // Para evitar subir imágenes repetidas
    var selectedCategory by remember { mutableStateOf<DocumentReference?>(null) }
    var selectedCategoryName by remember { mutableStateOf("") }
    val schedule = remember { mutableStateListOf<Map<String, Any>>() }
    var selectedDay by remember { mutableStateOf("Selecciona un día") }
    var startTime by remember { mutableStateOf<Long?>(null) }
    var endTime by remember { mutableStateOf<Long?>(null) }

    val categories by createViewModel.categories.collectAsState(initial = emptyList())
    var isCategoryDropdownExpanded by remember { mutableStateOf(false) }
    var isDayDropdownExpanded by remember { mutableStateOf(false) }
    val context = LocalContext.current

    Scaffold(
        topBar = {
//            Header(title = "Tu Taller UCC")
        },
        content = { innerPadding ->
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .padding(0.dp)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                CreationChips(
                    selectedOption = selectedOption,
                    onOptionSelected = { selectedOption = it }
                )

                when (selectedOption) {
                    "Taller" -> WorkshopForm(navController, createViewModel)
//                    "Comunidad" -> CommunityForm()
//                    "Noticia Oficial" -> OfficialNewsForm()
//                    "Publicación" -> PostForm()
                }

//                // Campos del formulario
//                TextField(
//                    value = name,
//                    onValueChange = { name = it },
//                    label = { Text("Nombre del Taller") },
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .padding(top = 16.dp),
//                )
//
//                TextField(
//                    value = description,
//                    onValueChange = { description = it },
//                    label = { Text("Descripción") },
//                    modifier = Modifier
//                        .fillMaxWidth()
//
//                )
//
//                // Selector de imágenes
//                ImagePickerButton { uri ->
//                    if (uri != null && !selectedImageUris.contains(uri)) {
//                        selectedImageUris.add(uri)
//                    }
//                }
//
//                if (selectedImageUris.isNotEmpty()) {
//                    val pagerState = rememberPagerState(pageCount = { selectedImageUris.size })
//
//                    Column(
//                        modifier = Modifier
//                            .fillMaxWidth(),
//                        horizontalAlignment = Alignment.CenterHorizontally
//                    ) {
//                        // Carrusel
//                        HorizontalPager(
//                            state = pagerState,
//                            modifier = Modifier
//                                .fillMaxWidth()
//                                .height(160.dp), // Altura ajustada para que sea compacto
//                            pageSpacing = 16.dp, // Espaciado entre páginas
//                            pageSize = PageSize.Fixed(160.dp),
//                            beyondViewportPageCount = 2, // Mostrar parcialmente las imágenes adyacentes
//                            verticalAlignment = Alignment.CenterVertically // Centrar verticalmente las imágenes
//                        ) { page ->
//                            Box(
//                                modifier = Modifier
//                                    .fillMaxSize()
//                                    .clip(MaterialTheme.shapes.medium) // Bordes redondeados
//                            ) {
//                                Image(
//                                    painter = rememberAsyncImagePainter(selectedImageUris[page]),
//                                    contentDescription = "Imagen seleccionada",
//                                    modifier = Modifier
//                                        .fillMaxSize()
//                                        .clip(MaterialTheme.shapes.medium), // Bordes redondeados
//                                    contentScale = ContentScale.Crop // Asegurar que la imagen llene el espacio
//                                )
//                            }
//                        }
//
//                        // Indicador de posición actual (propiedad del PagerState)
//                        Text(
//                            text = "${pagerState.currentPage + 1} / ${selectedImageUris.size}",
//                            style = MaterialTheme.typography.bodyMedium,
//                            modifier = Modifier.padding(top = 8.dp)
//                        )
//                    }
//                }
//
//
//                // Botón para subir imágenes
//                Button(
//                    onClick = {
//                        // Filtrar imágenes que aún no han sido subidas
//                        val imagesToUpload = selectedImageUris.filter { it !in uploadedImagesSet }
//                        var successfullyUploadedCount = 0
//
//                        if (imagesToUpload.isNotEmpty()) {
//                            imagesToUpload.forEach { uri ->
//                                uploadImageToImgur(uri, context) { imageUrl ->
//                                    if (imageUrl != null) {
//                                        uploadedImageUrls.add(imageUrl)
//                                        uploadedImagesSet.add(uri) // Marcar como subida
//                                        successfullyUploadedCount++
//
//                                        // Si se completan todas las subidas, mostrar un resumen
//                                        if (successfullyUploadedCount == imagesToUpload.size) {
//                                            Toast.makeText(
//                                                context,
//                                                "Todas las imágenes se subieron exitosamente (${uploadedImageUrls.size} en total).",
//                                                Toast.LENGTH_LONG
//                                            ).show()
//                                        }
//                                    } else {
//                                        // Notificar al usuario si ocurre un error al subir
//                                        Toast.makeText(
//                                            context,
//                                            "Error al subir una imagen. Inténtalo nuevamente.",
//                                            Toast.LENGTH_SHORT
//                                        ).show()
//                                    }
//                                }
//                            }
//                        } else {
//                            // Notificar si no hay imágenes nuevas para subir
//                            Toast.makeText(
//                                context,
//                                "No hay imágenes nuevas para subir.",
//                                Toast.LENGTH_SHORT
//                            ).show()
//                        }
//                    },
//                    enabled = selectedImageUris.isNotEmpty()
//                ) {
//                    Text("Subir Imágenes")
//                }
//
//
//                // Selector de categorías
//                Text("Selecciona una Categoría:", style = MaterialTheme.typography.bodyMedium)
//                ExposedDropdownMenuBox(
//                    expanded = isCategoryDropdownExpanded,
//                    onExpandedChange = { isCategoryDropdownExpanded = !isCategoryDropdownExpanded }
//                ) {
//                    TextField(
//                        value = selectedCategoryName.ifEmpty { "Selecciona una categoría" },
//                        onValueChange = {},
//                        readOnly = true,
//                        label = { Text("Categorías") },
//                        trailingIcon = {
//                            ExposedDropdownMenuDefaults.TrailingIcon(isCategoryDropdownExpanded)
//                        },
//                        modifier = Modifier
//                            .menuAnchor()
//                            .fillMaxWidth()
//                    )
//                    ExposedDropdownMenu(
//                        expanded = isCategoryDropdownExpanded,
//                        onDismissRequest = { isCategoryDropdownExpanded = false }
//                    ) {
//                        categories.forEach { category ->
//                            DropdownMenuItem(
//                                text = { Text(category.name) },
//                                onClick = {
//                                    selectedCategory = category.reference
//                                    selectedCategoryName = category.name
//                                    isCategoryDropdownExpanded = false
//                                }
//                            )
//                        }
//                    }
//                }
//
//                // Selector de días
//                Text("Selecciona un Día:", style = MaterialTheme.typography.bodyMedium)
//                ExposedDropdownMenuBox(
//                    expanded = isDayDropdownExpanded,
//                    onExpandedChange = { isDayDropdownExpanded = !isDayDropdownExpanded }
//                ) {
//                    TextField(
//                        value = selectedDay,
//                        onValueChange = {},
//                        readOnly = true,
//                        label = { Text("Días de la Semana") },
//                        trailingIcon = {
//                            ExposedDropdownMenuDefaults.TrailingIcon(isDayDropdownExpanded)
//                        },
//                        modifier = Modifier
//                            .menuAnchor()
//                            .fillMaxWidth()
//                    )
//                    ExposedDropdownMenu(
//                        expanded = isDayDropdownExpanded,
//                        onDismissRequest = { isDayDropdownExpanded = false }
//                    ) {
//                        val daysOfWeek = listOf("Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado", "Domingo")
//                        daysOfWeek.forEach { day ->
//                            DropdownMenuItem(
//                                text = { Text(day) },
//                                onClick = {
//                                    selectedDay = day
//                                    isDayDropdownExpanded = false
//                                }
//                            )
//                        }
//                    }
//                }
//
//                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
//                    TimePickerButton(
//                        text = "Hora Inicio",
//                        selectedTime = startTime,
//                        onTimeSelected = { startTime = it }
//                    )
//                    TimePickerButton(
//                        text = "Hora Fin",
//                        selectedTime = endTime,
//                        onTimeSelected = { endTime = it }
//                    )
//                }
//
//                Button(
//                    onClick = {
//                        if (selectedDay != "Selecciona un día" && startTime != null && endTime != null) {
//                            schedule.add(
//                                mapOf(
//                                    "day" to selectedDay,
//                                    "startTime" to startTime!!,
//                                    "endTime" to endTime!!
//                                )
//                            )
//                            selectedDay = "Selecciona un día"
//                            startTime = null
//                            endTime = null
//                        }
//                    }
//                ) {
//                    Text("Agregar al Horario")
//                }
//
//                // Lista de horarios agregados
//                Text("Horarios Agregados:", style = MaterialTheme.typography.bodyMedium)
//                schedule.forEach { entry ->
//                    Text(
//                        "Día: ${entry["day"]}, Inicio: ${formatTime(entry["startTime"] as Long)}, Fin: ${formatTime(entry["endTime"] as Long)}"
//                    )
//                }
//
//                // Botón para guardar
//                Button(
//                    onClick = {
//                        if (uploadedImageUrls.isNotEmpty() && selectedCategory != null) {
//                            createViewModel.createWorkshop(
//                                name = name,
//                                description = description,
//                                imageUrls = uploadedImageUrls.toList(),
//                                categoryReference = selectedCategory!!,
//                                schedule = schedule
//                            )
//                            navController.popBackStack()
//                        } else {
//                            Toast.makeText(context, "Por favor, sube imágenes y selecciona una categoría", Toast.LENGTH_SHORT).show()
//                        }
//                    },
//                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
//                ) {
//                    Text("Crear Workshop")
//                }
            }
        }
    )
}



