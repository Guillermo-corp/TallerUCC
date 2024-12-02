package com.example.tallerucc.pages.composables

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.tallerucc.ui.theme.DarkGrey
import com.example.tallerucc.ui.theme.LightBlue
import com.example.tallerucc.ui.theme.Typography
import com.google.firebase.Timestamp
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun NotificationCard(
    title: String,
    message: String,
    timestamp: Timestamp,
    isRead: Boolean,
    communityLogo: String?, // Agregar el logo de la comunidad
    onClick: () -> Unit
) {
    val formattedDate = remember(timestamp) {
        val date = timestamp.toDate()
        SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault()).format(date)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp, horizontal = 8.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isRead) Color.LightGray else Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Logo de la comunidad
            if (communityLogo != null) {
                AsyncImage(
                    model = communityLogo,
                    contentDescription = "Logo de la comunidad",
                    modifier = Modifier
                        .size(48.dp)
                        .padding(end = 8.dp)
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .padding(end = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Logo",
                        style = Typography.bodySmall,
                        fontSize = 12.sp,
                        color = DarkGrey
                    )
                }
            }

            // Información de la notificación
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Título
                Text(
                    text = title,
                    style = Typography.titleSmall,
                    fontSize = 16.sp,
                    color = if (isRead) DarkGrey.copy(alpha = 0.8f) else LightBlue,
                    fontWeight = FontWeight.Bold
                )

                // Mensaje
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = message,
                    style = Typography.bodySmall,
                    fontSize = 14.sp,
                    color = DarkGrey.copy(alpha = 0.8f)
                )

                // Fecha
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = formattedDate,
                    style = Typography.bodySmall,
                    fontSize = 12.sp,
                    color = DarkGrey.copy(alpha = 0.6f)
                )
            }
        }
    }
}
