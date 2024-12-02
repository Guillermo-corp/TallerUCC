package com.example.tallerucc.pages.create

import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CardElevation
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.ExposedDropdownMenuDefaults.outlinedTextFieldColors
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.InputChip
import androidx.compose.material3.InputChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.tallerucc.pages.composables.ImagePickerButton
import com.example.tallerucc.pages.composables.ImagePickerPreview
import com.example.tallerucc.repository.uploadImageToImgur
import com.example.tallerucc.ui.theme.DarkBlue
import com.example.tallerucc.ui.theme.DarkGrey
import com.example.tallerucc.ui.theme.LightBlue
import com.example.tallerucc.ui.theme.LightGreen
import com.example.tallerucc.ui.theme.Typography
import com.example.tallerucc.ui.theme.White
import com.example.tallerucc.viewModel.CreateViewModel
import com.google.firebase.auth.FirebaseAuth

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun CommunityForm(navController: NavController, createViewModel: CreateViewModel) {
    // Estados locales del formulario
    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var logoUri by remember { mutableStateOf<Uri?>(null) }
    var bannerUri by remember { mutableStateOf<Uri?>(null) }
    val tags = remember { mutableStateListOf<String>() } // Lista de tags
    var selectedTag by remember { mutableStateOf("") }
    var isTagDropdownExpanded by remember { mutableStateOf(false) } // Controla la visibilidad del dropdown
    val predefinedTags = listOf("Deportes", "Arte", "Cultura", "Tecnología", "Educación") // Lista de etiquetas predefinidas
    var logoUrl by remember { mutableStateOf("") }
    var bannerUrl by remember { mutableStateOf("") }
    val context = LocalContext.current
    val uploadedImagesSet = remember { mutableStateMapOf<Uri, String>() } // Track de imágenes subidas
    val currentUser = FirebaseAuth.getInstance().currentUser

    var isNameAvailable by remember { mutableStateOf<Boolean?>(null) } // null indica que no se ha validado aún
    var isValidatingName by remember { mutableStateOf(false) } // Estado de carga para la validación

    // Estado para marcar la comunidad como oficial
    var isOfficial by remember { mutableStateOf(false) }

    // Obtener los roles del usuario
    val userRoles by createViewModel.userRoles.collectAsState()



    Text(
        text = "Crea una nueva Comunidad",
        style = Typography.titleMedium,
        color = DarkBlue,
        modifier = Modifier.padding(horizontal = 16.dp)
    )

    Spacer(modifier = Modifier.height(0.dp))

    // Campo: Nombre de la comunidad
    OutlinedTextField(
        value = name,
        onValueChange = {
            name = it
            isValidatingName = true

            // Solo validar si el nombre no está vacío
            if (it.isNotEmpty()) {
                createViewModel.isCommunityNameAvailable(it) { isAvailable ->
                    isNameAvailable = isAvailable
                    isValidatingName = false
                }
            } else {
                isNameAvailable = null
                isValidatingName = false
            }
        },
        label = {
            Text("Nombre de la Comunidad")
        },
        isError = isNameAvailable == false, // Marca el campo como error si el nombre no está disponible
        supportingText = {
            when {
                isValidatingName -> Text("Verificando disponibilidad...", style = Typography.bodySmall)
                isNameAvailable == true -> Text("Nombre disponible", color = LightGreen, style = Typography.bodySmall)
                isNameAvailable == false -> Text("Nombre ocupado", color = Color.Red, style = Typography.bodySmall)
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        colors = outlinedTextFieldColors(
            unfocusedBorderColor = DarkBlue,
            focusedBorderColor = if (isNameAvailable == false) Color.Red else DarkBlue
        )
    )


    // Campo: Descripción
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

    HorizontalDivider(
        color = LightBlue.copy(alpha = 0.2f),
        thickness = 1.dp,
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp)
    )

    // Campo: Tags (etiquetas)
    Text(
        text = "Añade etiquetas para la comunidad",
        style = Typography.bodyMedium,
        color = DarkBlue,
        modifier = Modifier.padding(horizontal = 16.dp)
    )
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp)
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(0.dp)
    ) {
        ExposedDropdownMenuBox(
            expanded = isTagDropdownExpanded,
            onExpandedChange = { isTagDropdownExpanded = !isTagDropdownExpanded },
            modifier = Modifier
                .weight(2f)
                .fillMaxHeight(),
        ) {
            TextField(
                value = selectedTag,
                onValueChange = {},
                readOnly = true,
                label = {
                    Text(
                        text = "Seleccionar etiqueta",
                        style = Typography.bodySmall,
                        color = DarkBlue,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                },
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = isTagDropdownExpanded)
                },
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth()
                    .fillMaxHeight(),
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
                expanded = isTagDropdownExpanded,
                onDismissRequest = { isTagDropdownExpanded = false }
            ) {
                predefinedTags.forEach { tag ->
                    DropdownMenuItem(
                        text = {
                            Text(
                                tag, style = Typography.bodyMedium, color = DarkGrey
                            )
                        },
                        modifier = Modifier.padding(horizontal = 8.dp),
                        onClick = {
                            selectedTag = tag
                            isTagDropdownExpanded = false
                        }
                    )
                }
            }
        }

        Button(
            shape = RoundedCornerShape(topEnd = 8.dp, bottomEnd = 8.dp),
            onClick = {
                if (selectedTag.isNotBlank() && !tags.contains(selectedTag)) {
                    tags.add(selectedTag)
                    selectedTag = "" // Limpia la selección después de añadir
                }
            },
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
        ) {
            Text(
                text = "Añadir",
                style = Typography.titleSmall,
            )
        }
    }

    // Mostrar checkbox solo para administradores
    if ("admin" in userRoles) {
        Spacer(modifier = Modifier.height(0.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp, bottom = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
//            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Marcar como comunidad oficial",
                style = Typography.bodyMedium,
                color = DarkBlue,
                modifier = Modifier.padding(start = 0.dp)
            )
            Checkbox(
                checked = isOfficial,
                onCheckedChange = { isOfficial = it },
                colors = CheckboxDefaults.colors(
                    checkedColor = LightBlue,
                    uncheckedColor = DarkBlue
                ),
                modifier = Modifier.padding(start = 16.dp)
            )
        }
    }

    // Mostrar lista de etiquetas como chips
    if (tags.isNotEmpty()) {
        Text(
            modifier = Modifier
                .padding(horizontal = 16.dp),
            text = "Etiquetas seleccionadas:",
            style = Typography.bodyMedium,
            color = DarkBlue
        )
        androidx.compose.foundation.layout.FlowRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            tags.forEach { tag ->
                InputChip(
                    selected = false,
                    onClick = { /* Acción opcional al hacer clic en el chip */ },
                    label = {
                        Text(
                            text = tag,
                            style = Typography.bodySmall,
                        )

                    },
                    trailingIcon = {
                        Text(
                            text = "×",
                            modifier = Modifier.clickable { tags.remove(tag) },
                            color = LightBlue
                        )
                    },
                    modifier = Modifier.padding(vertical = 0.dp),
                    colors = InputChipDefaults.inputChipColors(
                        containerColor = LightBlue.copy(alpha = 0.03f),
                        labelColor = LightBlue,
                        leadingIconColor = LightBlue,
                        trailingIconColor = LightBlue,
                    ),
                    border = BorderStroke(1.dp, LightBlue)
                )
            }
        }
    }

    HorizontalDivider(
        color = LightBlue.copy(alpha = 0.2f),
        thickness = 1.dp,
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp)
    )

    // Subida de Logo
    Text(
        modifier = Modifier.padding(horizontal = 16.dp),
        text = "Sube el logo de la comunidad",
        style = Typography.bodyMedium,
        color = DarkBlue
    )


    Box(
        modifier = Modifier
            .fillMaxWidth(), // Asegura que el contenedor ocupe todo el ancho disponible
        contentAlignment = Alignment.Center // Centra el contenido dentro del Box
    ) {
        ImagePickerPreview(
            imageUri = logoUri,
            onImageSelected = { uri -> logoUri = uri },
            shape = CircleShape,
            widthFill = false, // Ajusta a fillMaxWidth
            height = 170 // Altura del banner en dp
        )
    }

    // Subida de Banner
    Text(
        modifier = Modifier.padding(horizontal = 16.dp),
        text = "Sube el banner de la comunidad",
        style = Typography.bodyMedium,
        color = DarkBlue
    )

    ImagePickerPreview(
        imageUri = bannerUri,
        onImageSelected = { uri -> bannerUri = uri },
        shape = RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp),
        widthFill = true, // Ajusta a fillMaxWidth
        height = 220 // Altura del banner en dp
    )

    Spacer(modifier = Modifier.height(16.dp))

    // Botón para subir imágenes
    Button(
        onClick = {
            val imagesToUpload = listOfNotNull(logoUri, bannerUri).filter { it !in uploadedImagesSet.keys }
            if (imagesToUpload.isNotEmpty()) {
                imagesToUpload.forEach { uri ->
                    createViewModel.uploadImage(uri, context) { imageUrl ->
                        if (imageUrl != null) {
                            uploadedImagesSet[uri] = imageUrl
                            if (uri == logoUri) logoUrl = imageUrl
                            if (uri == bannerUri) bannerUrl = imageUrl
                            Toast.makeText(context, "Imagen subida correctamente.", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(context, "Error al subir la imagen.", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            } else {
                Toast.makeText(context, "No hay imágenes nuevas para subir.", Toast.LENGTH_SHORT).show()
            }
        },
        enabled = listOfNotNull(logoUri, bannerUri).isNotEmpty(),
        modifier = Modifier.padding(16.dp)
    ) {
        Text(
            text = "Confirmar Imágenes",
            style = Typography.titleSmall
        )
    }


    HorizontalDivider(
        color = LightBlue.copy(alpha = 0.2f),
        thickness = 1.dp,
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp)
    )

    // Botón para guardar la comunidad
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 48.dp, start = 16.dp, end = 16.dp)
            .clickable(
                enabled = !(
                        logoUrl.isNotEmpty() &&
                                bannerUrl.isNotEmpty() &&
                                isNameAvailable == true
                        )
            ) {
                // Mostrar mensaje si el botón está deshabilitado
                Toast
                    .makeText(
                        context,
                        "Completa todos los campos obligatorios antes de crear la comunidad.",
                        Toast.LENGTH_SHORT
                    )
                    .show()
            }
    ) {
        Button(
            shape = RoundedCornerShape(8.dp),
            onClick = {
                if (name.isNotEmpty() && description.isNotEmpty() && logoUrl.isNotEmpty() && bannerUrl.isNotEmpty()) {
                    if (currentUser != null) {
                        val email = currentUser.email ?: ""
                        val userId = currentUser.uid

                        createViewModel.createCommunity(
                            name = name,
                            description = description,
                            iconUrl = logoUrl,
                            bannerUrl = bannerUrl,
                            tags = tags.toList(),
                            userEmail = email, // Pasar el email del usuario actual
                            userId = userId, // Pasar el UID para actualizar su documento en Firestore
                            isOfficial = isOfficial, // Pasar el estado del checkbox
                        )
                        Toast.makeText(
                            context,
                            "Comunidad creada exitosamente.",
                            Toast.LENGTH_SHORT
                        ).show()
                        navController.popBackStack()
                    } else {
                        Toast.makeText(
                            context,
                            "Error: No se pudo identificar al usuario actual.",
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
            enabled = logoUrl.isNotEmpty() && bannerUrl.isNotEmpty() && isNameAvailable == true,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Crear Comunidad",
                style = Typography.titleMedium
            )
        }
    }


}


