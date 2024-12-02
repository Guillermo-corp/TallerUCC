package com.example.tallerucc.pages.composables

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tallerucc.ui.theme.DarkBlue
import com.example.tallerucc.ui.theme.LightBlue
import com.example.tallerucc.ui.theme.Typography
import com.example.tallerucc.ui.theme.White

@Composable
fun CategoryItem(
    categoryName: String,
    iconRes: Int,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .aspectRatio(1f)
            .clickable { onClick() },
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(
            containerColor = LightBlue.copy(alpha = 0.1f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Ícono dentro de un círculo
            Box(
                modifier = Modifier
                    .size(88.dp)
                    .background(LightBlue, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                // Usar painterResource para cargar el drawable
                Image(
                    painter = painterResource(id = iconRes),
                    contentDescription = "Icon for $categoryName",
                    modifier = Modifier.size(56.dp), // Ajusta el tamaño según sea necesario
                    colorFilter = ColorFilter.tint(White)
                )
            }

            // Espaciado
            Spacer(modifier = Modifier.height(16.dp))

            // Nombre de la categoría
            Text(
                text = categoryName,
                style = Typography.titleMedium,
                fontSize = 16.sp,
                color = DarkBlue
            )
        }
    }
}


