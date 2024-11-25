package com.example.tallerucc.pages

import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.OverscrollEffect
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.overscroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.tallerucc.pages.composables.Header
import com.example.tallerucc.pages.composables.ImagePickerButton
import com.example.tallerucc.pages.composables.TimePickerButton
import com.example.tallerucc.pages.composables.formatTime
import com.example.tallerucc.repository.uploadImageToImgur
import com.example.tallerucc.ui.theme.Typography
import com.example.tallerucc.viewModel.AuthState
import com.example.tallerucc.viewModel.AuthViewModel
import com.example.tallerucc.viewModel.CreateViewModel
import com.google.firebase.firestore.DocumentReference

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreatePage(navController: NavController, createViewModel: CreateViewModel) {
    // Estados del formulario
    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var uploadedImageUrl by remember { mutableStateOf<String?>(null) }
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
            Header(title = "Crear Workshop")
        },
        content = { innerPadding ->
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .padding(start = 16.dp, end = 16.dp)
                    .fillMaxSize() // Define un tamaño claro
                    .verticalScroll(rememberScrollState()), // Habilita scroll vertical
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Campos del formulario
                TextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nombre del Taller") },
                    modifier = Modifier.fillMaxWidth().padding(top = 16.dp)
                )

                TextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Descripción") },
                    modifier = Modifier.fillMaxWidth()
                )

                // Selector de imágenes
                ImagePickerButton { uri ->
                    selectedImageUri = uri
                    uploadedImageUrl = null
                }

                // Vista previa de la imagen seleccionada
                selectedImageUri?.let { uri ->
                    Image(
                        painter = rememberAsyncImagePainter(uri),
                        contentDescription = "Vista previa de la imagen",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                    )
                }

                Button(
                    onClick = {
                        selectedImageUri?.let { uri ->
                            uploadImageToImgur(uri, context) { imageUrl ->
                                uploadedImageUrl = imageUrl
                                if (imageUrl == null) {
                                    Toast.makeText(context, "Error subiendo la imagen", Toast.LENGTH_SHORT).show()
                                } else {
                                    Toast.makeText(context, "Imagen subida con éxito", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    },
                    enabled = selectedImageUri != null
                ) {
                    Text("Subir Imagen")
                }


                uploadedImageUrl?.let { url ->
                    Text(text = "Imagen subida: $url")
                }

                // Selector de categorías
                Text("Selecciona una Categoría:", style = MaterialTheme.typography.bodyMedium)
                ExposedDropdownMenuBox(
                    expanded = isCategoryDropdownExpanded,
                    onExpandedChange = { isCategoryDropdownExpanded = !isCategoryDropdownExpanded }
                ) {
                    TextField(
                        value = selectedCategoryName.ifEmpty { "Selecciona una categoría" },
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Categorías") },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(isCategoryDropdownExpanded)
                        },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = isCategoryDropdownExpanded,
                        onDismissRequest = { isCategoryDropdownExpanded = false }
                    ) {
                        categories.forEach { category ->
                            DropdownMenuItem(
                                text = { Text(category.name) },
                                onClick = {
                                    selectedCategory = category.reference
                                    selectedCategoryName = category.name
                                    isCategoryDropdownExpanded = false
                                }
                            )
                        }
                    }
                }

                // Selector de días
                Text("Selecciona un Día:", style = MaterialTheme.typography.bodyMedium)
                ExposedDropdownMenuBox(
                    expanded = isDayDropdownExpanded,
                    onExpandedChange = { isDayDropdownExpanded = !isDayDropdownExpanded }
                ) {
                    TextField(
                        value = selectedDay,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Días de la Semana") },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(isDayDropdownExpanded)
                        },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = isDayDropdownExpanded,
                        onDismissRequest = { isDayDropdownExpanded = false }
                    ) {
                        val daysOfWeek = listOf("Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado", "Domingo")
                        daysOfWeek.forEach { day ->
                            DropdownMenuItem(
                                text = { Text(day) },
                                onClick = {
                                    selectedDay = day
                                    isDayDropdownExpanded = false
                                }
                            )
                        }
                    }
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
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
                    Text("Agregar al Horario")
                }

                // Lista de horarios agregados
                Text("Horarios Agregados:", style = MaterialTheme.typography.bodyMedium)
                schedule.forEach { entry ->
                    Text(
                        "Día: ${entry["day"]}, Inicio: ${formatTime(entry["startTime"] as Long)}, Fin: ${formatTime(entry["endTime"] as Long)}"
                    )
                }

                // Botón para guardar
                Button(
                    onClick = {
                        if (uploadedImageUrl != null && selectedCategory != null) {
                            createViewModel.createWorkshop(
                                name = name,
                                description = description,
                                imageUrl = uploadedImageUrl!!,
                                categoryReference = selectedCategory!!,
                                schedule = schedule
                            )
                            navController.popBackStack()
                        } else {
                            Toast.makeText(context, "Por favor, selecciona una imagen y una categoría", Toast.LENGTH_SHORT).show()
                        }
                    },
                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
                ) {
                    Text("Crear Workshop")
                }
            }
        }
    )
}


