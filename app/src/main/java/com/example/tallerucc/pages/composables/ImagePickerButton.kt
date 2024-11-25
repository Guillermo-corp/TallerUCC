package com.example.tallerucc.pages.composables

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.tallerucc.ui.theme.Typography

@Composable
fun ImagePickerButton(onImageSelected: (Uri?) -> Unit) {
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        onImageSelected(uri)
    }

    Button(
        modifier = Modifier
            .padding(horizontal = 16.dp),
        onClick = { launcher.launch("image/*") }
    ) {
        Text(
            text = "Seleccionar Imagen",
            style = Typography.bodyMedium
        )
    }
}
