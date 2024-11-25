package com.example.tallerucc.pages.create

import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
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
import androidx.compose.material3.ExposedDropdownMenuDefaults.outlinedTextFieldColors
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
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
import com.example.tallerucc.repository.uploadImageToImgur
import com.example.tallerucc.ui.theme.DarkBlue
import com.example.tallerucc.ui.theme.DarkGrey
import com.example.tallerucc.ui.theme.LightBlue
import com.example.tallerucc.ui.theme.Typography
import com.example.tallerucc.ui.theme.White
import com.example.tallerucc.viewModel.CreateViewModel
import com.google.firebase.firestore.DocumentReference

@OptIn(ExperimentalMaterial3Api::class, androidx.compose.foundation.ExperimentalFoundationApi::class)
@Composable
fun WorkshopForm(navController: NavController, createViewModel: CreateViewModel) {
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

    Text(
        text = "Crea un nuevo Taller",
        style = Typography.titleMedium,
        color = DarkBlue,
        modifier = Modifier.padding(horizontal = 16.dp)
    )

    Spacer(modifier = Modifier.height(8.dp))

    //    // Selector de categorías
//    Text("Selecciona una Categoría:", style = MaterialTheme.typography.bodyMedium)
    ExposedDropdownMenuBox(
        expanded = isCategoryDropdownExpanded,
        onExpandedChange = { isCategoryDropdownExpanded = !isCategoryDropdownExpanded },
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),

        ) {
        TextField(
            value = selectedCategoryName.ifEmpty {
                "Selecciona una categoría"
            },
            onValueChange = {},
            readOnly = true,
            label = {
                Text(
                    text = "Categoría del taller",
                    style = Typography.bodySmall,
                    color = DarkBlue,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(isCategoryDropdownExpanded)
            },
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth(),
            colors = TextFieldDefaults.colors(
                unfocusedIndicatorColor = DarkBlue,
                focusedIndicatorColor = DarkBlue,
                unfocusedContainerColor = LightBlue.copy(alpha = 0.07f),
                focusedContainerColor = LightBlue.copy(alpha = 0.07f),
                unfocusedTextColor = DarkGrey,
                focusedTextColor = DarkGrey
            )
        )
        ExposedDropdownMenu(
            modifier = Modifier
                .width(250.dp)
                .background(LightBlue.copy(alpha = 0.0f)),
            expanded = isCategoryDropdownExpanded,
            onDismissRequest = { isCategoryDropdownExpanded = false }
        ) {
            categories.forEach { category ->
                DropdownMenuItem(
                    text = {
                        Text(
                            text = category.name,
                            style = Typography.bodyMedium,
                            color = DarkGrey
                        )
                    },
                    modifier = Modifier.padding(horizontal = 8.dp),
                    onClick = {
                        selectedCategory = category.reference
                        selectedCategoryName = category.name
                        isCategoryDropdownExpanded = false
                    },
                )
            }
        }
    }

    // Campos del formulario
    OutlinedTextField(
        value = name,
        onValueChange = { name = it },
        label = { Text("Nombre del Taller") },
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        colors = outlinedTextFieldColors(
            unfocusedBorderColor = DarkBlue,
            focusedBorderColor = DarkBlue
        )
    )

    OutlinedTextField(
        value = description,
        onValueChange = { description = it },
        label = { Text("Descripción") },
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        colors = outlinedTextFieldColors(
            unfocusedBorderColor = DarkBlue,
            focusedBorderColor = DarkBlue
        ),
        minLines = 3,
        maxLines = 5
    )

    Spacer(modifier = Modifier.height(16.dp))

    // Selector de imágenes
    ImagePickerButton { uri ->
        if (uri != null && !selectedImageUris.contains(uri)) {
            selectedImageUris.add(uri)
        }
    }

    if (selectedImageUris.isNotEmpty()) {
        val pagerState = rememberPagerState(pageCount = { selectedImageUris.size })

        Column(
            modifier = Modifier
                .fillMaxWidth(),
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
        modifier = Modifier
            .padding(horizontal = 16.dp),
        onClick = {
            // Filtrar imágenes que aún no han sido subidas
            val imagesToUpload = selectedImageUris.filter { it !in uploadedImagesSet }
            var successfullyUploadedCount = 0

            if (imagesToUpload.isNotEmpty()) {
                imagesToUpload.forEach { uri ->
                    uploadImageToImgur(uri, context) { imageUrl ->
                        if (imageUrl != null) {
                            uploadedImageUrls.add(imageUrl)
                            uploadedImagesSet.add(uri) // Marcar como subida
                            successfullyUploadedCount++

                            // Si se completan todas las subidas, mostrar un resumen
                            if (successfullyUploadedCount == imagesToUpload.size) {
                                Toast.makeText(
                                    context,
                                    "Todas las imágenes se subieron exitosamente (${uploadedImageUrls.size} en total).",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        } else {
                            // Notificar al usuario si ocurre un error al subir
                            Toast.makeText(
                                context,
                                "Error al subir una imagen. Inténtalo nuevamente.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            } else {
                // Notificar si no hay imágenes nuevas para subir
                Toast.makeText(
                    context,
                    "No hay imágenes nuevas para subir.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        },
        enabled = selectedImageUris.isNotEmpty()
    ) {
        Text(
            text = "Subir Imágenes",
            style = Typography.bodyMedium
        )
    }

    Spacer(modifier = Modifier.height(16.dp))

    ExposedDropdownMenuBox(
        expanded = isDayDropdownExpanded,
        onExpandedChange = { isDayDropdownExpanded = !isDayDropdownExpanded },
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
    ) {
        TextField(
            value = selectedDay,
            onValueChange = {},
            readOnly = true,
            label = {
                Text(
                    text = "Días de la Semana",
                    style = Typography.bodySmall,
                    color = DarkBlue,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(isDayDropdownExpanded)
            },
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth(),
            colors = TextFieldDefaults.colors(
                unfocusedIndicatorColor = DarkBlue,
                focusedIndicatorColor = DarkBlue,
                unfocusedContainerColor = LightBlue.copy(alpha = 0.07f),
                focusedContainerColor = LightBlue.copy(alpha = 0.07f),
                unfocusedTextColor = DarkGrey,
                focusedTextColor = DarkGrey
            )
        )
        ExposedDropdownMenu(
            modifier = Modifier
                .width(250.dp),
            expanded = isDayDropdownExpanded,
            onDismissRequest = { isDayDropdownExpanded = false }
        ) {
            val daysOfWeek = listOf("Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado", "Domingo")
            daysOfWeek.forEach { day ->
                DropdownMenuItem(
                    text = {
                        Text(
                            text = day,
                            style = Typography.bodyMedium,
                            color = DarkGrey
                        )
                    },
                    modifier = Modifier.padding(horizontal = 8.dp),
                    onClick = {
                        selectedDay = day
                        isDayDropdownExpanded = false
                    }
                )
            }
        }
    }

    Row(
        modifier = Modifier
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        TimePickerButton(
            text = "Hora Inicio",
            selectedTime = startTime,
            onTimeSelected = { startTime = it }
        )
        TimePickerButton(
            text = "Hora Fin",
            selectedTime = endTime,
            onTimeSelected = { endTime = it }
        )
    }

    Button(
        modifier = Modifier
            .padding(horizontal = 16.dp),
        onClick = {
            if (selectedDay != "Selecciona un día" && startTime != null && endTime != null) {
                schedule.add(
                    mapOf(
                        "day" to selectedDay,
                        "startTime" to startTime!!,
                        "endTime" to endTime!!
                    )
                )
                selectedDay = "Selecciona un día"
                startTime = null
                endTime = null
            }
        }
    ) {
        Text(
            text = "Agregar al Horario",
            style = Typography.bodyLarge
        )
    }

    // Lista de horarios agregados
    Text(
        modifier = Modifier
            .padding(horizontal = 24.dp),
        text = "Horarios Agregados:",
        style = Typography.bodyMedium,
    )
    schedule.forEach { entry ->
        Text(
            modifier = Modifier
                .padding(horizontal = 24.dp),
            text = "Día: ${entry["day"]}, Inicio: ${formatTime(entry["startTime"] as Long)}, Fin: ${formatTime(entry["endTime"] as Long)}",
            style = Typography.bodySmall,
        )
    }

    Spacer(modifier = Modifier.height(16.dp))

    // Botón para guardar
    Button(

        onClick = {
            if (uploadedImageUrls.isNotEmpty() && selectedCategory != null) {
                createViewModel.createWorkshop(
                    name = name,
                    description = description,
                    imageUrls = uploadedImageUrls.toList(),
                    categoryReference = selectedCategory!!,
                    schedule = schedule
                )
                navController.popBackStack()
            } else {
                Toast.makeText(context, "Por favor, sube imágenes y selecciona una categoría", Toast.LENGTH_SHORT).show()
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 48.dp, start = 16.dp, end = 16.dp)
    ) {
        Text("Crear Workshop")
    }
}