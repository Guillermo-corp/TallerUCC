package com.example.tallerucc.pages.composables

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.DockedSearchBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.max
import androidx.compose.ui.unit.min
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import com.example.tallerucc.model.Community
import com.example.tallerucc.ui.theme.DarkBlue
import com.example.tallerucc.ui.theme.DarkGrey
import com.example.tallerucc.ui.theme.LightBlue
import com.example.tallerucc.ui.theme.Typography

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommunitySearchBar(
    followedCommunities: List<Community>,
    onCommunitySelected: (Community) -> Unit
) {
    var query by remember { mutableStateOf("") }
    var filteredCommunities by remember { mutableStateOf(followedCommunities) }
    var isDropdownExpanded by remember { mutableStateOf(false) }

    DockedSearchBar(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 50.dp , max = 400.dp)
            .padding(bottom = 16.dp, start = 16.dp, end = 16.dp),
        colors = SearchBarDefaults.colors(
            containerColor = LightBlue.copy(alpha = 0.06f),
            dividerColor = DarkBlue,
        ),
        query = query,
        onQueryChange = { newQuery ->
            query = newQuery
            filteredCommunities = followedCommunities.filter {
                it.name.contains(newQuery, ignoreCase = true)
            }
        },
        onSearch = {
            isDropdownExpanded = true
        },
        active = isDropdownExpanded,
        onActiveChange = { isActive ->
            isDropdownExpanded = isActive
        },
        placeholder = { Text("Buscar comunidad") },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search Icon",
                tint = DarkBlue
            )
        },
        trailingIcon = {
            if (isDropdownExpanded) {
                Icon(
                    modifier = Modifier.clickable {
                        if (query.isNotEmpty()) {
                            query = ""
                        } else {
                            isDropdownExpanded = false
                        }
                    },
                    imageVector = Icons.Default.Close,
                    contentDescription = "Close Icon",
                    tint = DarkBlue

                )
            }
        }
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
//                .heightIn(min = 50.dp, max = 200.dp)
        ) {
            if (filteredCommunities.isNotEmpty()) {
                items(filteredCommunities) { community ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                onCommunitySelected(community)
                                query = community.name
                                isDropdownExpanded = false
                            }
                            .padding(12.dp)
                    ) {
                        AsyncImage(
                            model = community.iconUrl,
                            contentDescription = "Ícono de la comunidad",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(Color.LightGray)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = community.name,
                            style = Typography.bodyMedium,
                            color = DarkGrey
                        )
                    }
                }
            } else {
                item {
                    Text(
                        text = "No se encontraron comunidades",
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                    )
                }
            }
        }
    }
}




