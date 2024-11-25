package com.example.tallerucc.pages.composables

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Create
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun FloatingActionButtonCustom(
    modifier: Modifier = Modifier,
    onFabClick: () -> Unit
) {
    FloatingActionButton(
        modifier = modifier
            .padding(0.dp)
            .size(75.dp),
        onClick = { onFabClick() },
        containerColor = MaterialTheme.colorScheme.primary,
        contentColor = MaterialTheme.colorScheme.onPrimary
    ) {
        Icon(Icons.Default.Create, contentDescription = "Create", modifier = Modifier.size(35.dp))
    }
}