package com.example.tallerucc.pages.create

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.SelectableChipColors
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import com.example.tallerucc.ui.theme.DarkBlue
import com.example.tallerucc.ui.theme.LightBlue
import com.example.tallerucc.ui.theme.Typography

@Composable
fun CreationTabs(
    selectedTab: Int,
    onTabSelected: (Int) -> Unit
) {
    val tabs = listOf("Workshop", "Comunidad", "Noticia Oficial", "Publicación")

    TabRow(
        selectedTabIndex = selectedTab,
        modifier = Modifier.fillMaxWidth()
    ) {
        tabs.forEachIndexed { index, title ->
            Tab(
                selected = selectedTab == index,
                onClick = { onTabSelected(index) },
                text = { Text(title) }
            )
        }
    }
}

@Composable
fun CreationChips(
    options: List<String>,
    selectedOption: String = "Comunidad",
    onOptionSelected: (String) -> Unit
) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier
            .padding(8.dp)
    ) {
        items(options) { option ->
            FilterChip(
                selected = selectedOption == option,
                onClick = { onOptionSelected(option) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = LightBlue.copy(alpha = 0.1f),
                ),
                border = BorderStroke(1.dp, DarkBlue),
                label = {
                    Text(
                        text = option,
                        style = Typography.bodyMedium,
                        color = DarkBlue
                    )
                }
            )
        }
    }
}



