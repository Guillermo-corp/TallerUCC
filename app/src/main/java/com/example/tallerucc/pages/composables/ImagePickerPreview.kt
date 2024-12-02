package com.example.tallerucc.pages.composables

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.tallerucc.R
import com.example.tallerucc.ui.theme.LightBlue
import com.example.tallerucc.ui.theme.LightGrey

@Composable
fun ImagePickerPreview(
    modifier: Modifier = Modifier,
    imageUri: Uri?,
    onImageSelected: (Uri?) -> Unit,
    shape: RoundedCornerShape,
    widthFill: Boolean = false, // Bandera para ajustar a fillMaxWidth
    height: Int = 120, // Altura en dp
    defaultIcon: Painter = painterResource(id = R.drawable.ic_camera_alt) // Ícono predeterminado
) {
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        onImageSelected(uri)
    }

    Box(
        modifier = modifier
            .then(if (widthFill) Modifier.fillMaxWidth() else Modifier.size(height.dp))
            .height(height.dp)
            .clip(shape)
            .background(if (imageUri == null) LightGrey else Color.Transparent)
            .clickable { launcher.launch("image/*") },
        contentAlignment = Alignment.Center
    ) {
        if (imageUri == null) {
            Icon(
                painter = defaultIcon,
                contentDescription = "Seleccionar imagen",
                tint = LightBlue,
                modifier = Modifier.size((height * 0.5).dp) // El ícono ocupa el 50% de la altura
            )
        } else {
            Image(
                painter = rememberAsyncImagePainter(imageUri),
                contentDescription = "Imagen seleccionada",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

