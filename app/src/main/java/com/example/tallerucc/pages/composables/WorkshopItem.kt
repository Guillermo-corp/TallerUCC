package com.example.tallerucc.pages.composables

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.tallerucc.model.Workshop
import com.example.tallerucc.ui.theme.DarkBlue
import com.example.tallerucc.ui.theme.LightBlue
import com.example.tallerucc.ui.theme.Typography

@Composable
fun WorkshopItem(
    workshop: Workshop,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
//                .padding(16.dp),
//            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Imagen del workshop
            AsyncImage(
                model = workshop.imageUrl, // URL de la imagen
                contentDescription = "Imagen del workshop",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(shape = RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp)),
//                alignment = Alignment.Center
            )

            // Nombre del workshop
            Text(
                text = workshop.name,
                style = Typography.titleMedium,
                color = DarkBlue,
                modifier = Modifier
                    .padding(start = 16.dp, top = 16.dp)
            )

            // Botón para ver detalles
            Button(
                onClick = onClick,
                colors = ButtonDefaults.buttonColors(
                    containerColor = LightBlue,
                ),
                modifier = Modifier
                    .align(Alignment.Start)
                    .padding(start = 16.dp, bottom = 8.dp, top = 8.dp),
            ) {
                Text("Ver detalles", style = Typography.bodyMedium)
            }
        }
    }
}



