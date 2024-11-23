package com.example.tallerucc.pages.composables

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.tallerucc.model.Workshop
import com.example.tallerucc.ui.theme.Typography

@Composable
fun WorkshopItem(workshop: Workshop, onClick: () -> Unit) {
    Card(modifier = Modifier.padding(8.dp).clickable { onClick() }) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = workshop.name, style = Typography.titleMedium)
            Text(text = workshop.description, style = Typography.bodyMedium)
        }
    }
}


