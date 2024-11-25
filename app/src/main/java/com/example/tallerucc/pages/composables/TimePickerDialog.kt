package com.example.tallerucc.pages.composables

import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimePickerDialog(
    onDismissRequest: () -> Unit,
    onTimeSelected: (Long) -> Unit
) {
    val state = rememberTimePickerState(initialHour = 12, initialMinute = 0)
    androidx.compose.material3.AlertDialog(
        onDismissRequest = { onDismissRequest() },
        title = {
            Text("Selecciona una hora", style = MaterialTheme.typography.titleMedium)
        },
        text = {
            TimePicker(state = state) // Time Picker de Material Design 3
        },
        confirmButton = {
            Button(onClick = {
                val selectedTimeInMillis = (state.hour * 60 * 60 * 1000 + state.minute * 60 * 1000).toLong()
                onTimeSelected(selectedTimeInMillis)
            }) {
                Text("Aceptar")
            }
        },
        dismissButton = {
            Button(onClick = { onDismissRequest() }) {
                Text("Cancelar")
            }
        }
    )
}
