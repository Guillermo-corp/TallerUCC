package com.example.tallerucc.pages.composables

import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Icon
import com.example.tallerucc.R
import com.example.tallerucc.model.Category

// Definición de íconos por categoría
val categoryIcons = mapOf(
    "Deportes" to R.drawable.ic_deportes,
    "Artes" to R.drawable.ic_artes,
    "Idiomas" to R.drawable.ic_idiomas,
    "Cultural" to R.drawable.ic_cultural,
    "Social" to R.drawable.ic_social,
    "Pastoral" to R.drawable.ic_pastoral
)

// Composable para mostrar el grid de categorías
@Composable
fun CategoryGrid(
    categories: List<Category>, // Lista de categorías a mostrar
    onCategoryClick: (String) -> Unit // Callback al hacer clic en una categoría
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2), // Dos columnas en la cuadrícula
        verticalArrangement = Arrangement.spacedBy(16.dp), // Espaciado vertical
        horizontalArrangement = Arrangement.spacedBy(16.dp), // Espaciado horizontal
        modifier = Modifier.fillMaxSize() // Ocupar todo el tamaño disponible
    ) {
        items(categories) { category ->
            // Obtener el ícono basado en el nombre de la categoría
            val iconRes = categoryIcons[category.name] ?: R.drawable.logo2_blanco_ch // Ícono predeterminado
            CategoryItem(
                categoryName = category.name,
                iconRes = iconRes, // Pasar el recurso drawable
                onClick = { onCategoryClick(category.id) }
            )
        }
    }
}


