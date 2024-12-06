package com.example.tallerucc.pages.composables

import android.app.TimePickerDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.tallerucc.ui.theme.LightBlue
import com.example.tallerucc.ui.theme.Typography
import java.util.Locale

@Composable
fun TimePickerButton(
    text: String,
    selectedTime: Long?,
    onTimeSelected: (Long) -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }

    Button(
        onClick = { showDialog = true },
        colors = ButtonDefaults.buttonColors(
            containerColor = LightBlue,
        )
    ) {
        Text(
            text = selectedTime?.let { formatTime(it) } ?: text,
            style = Typography.titleSmall
        )
    }

    if (showDialog) {
        TimePickerDialog(
            onDismissRequest = { showDialog = false },
            onTimeSelected = { selectedTime ->
                onTimeSelected(selectedTime)
                showDialog = false // Cerrar el diálogo después de seleccionar
            },
        )
    }
}

fun formatTime(milliseconds: Long): String {
    val hours = (milliseconds / (60 * 60 * 1000)) % 24
    val minutes = (milliseconds / (60 * 1000)) % 60
    return String.format(Locale.getDefault(),"%02d:%02d", hours, minutes)
}


