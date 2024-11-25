package com.example.tallerucc.pages.composables

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun ImagePickerButton(onImageSelected: (Uri?) -> Unit) {
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        onImageSelected(uri)
    }

    Button(onClick = { launcher.launch("image/*") }) {
        Text("Seleccionar Imagen")
    }
}
