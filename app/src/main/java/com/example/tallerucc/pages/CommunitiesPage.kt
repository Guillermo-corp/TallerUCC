package com.example.tallerucc.pages

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.tallerucc.navigation.navItems
import com.example.tallerucc.pages.composables.BottomNavBar
import com.example.tallerucc.pages.composables.CommunityCard
import com.example.tallerucc.pages.composables.FloatingActionButtonCustom
import com.example.tallerucc.pages.composables.Header
import com.example.tallerucc.ui.theme.DarkBlue
import com.example.tallerucc.ui.theme.LightBlue
import com.example.tallerucc.ui.theme.Typography
import com.example.tallerucc.viewModel.AuthState
import com.example.tallerucc.viewModel.AuthViewModel
import com.example.tallerucc.viewModel.CommunityViewModel
import com.example.tallerucc.viewModel.CreateViewModel
import com.example.tallerucc.viewModel.NavigationViewModel

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CommunitiesPage(
    modifier: Modifier = Modifier,
    navController: NavController,
    navigationViewModel: NavigationViewModel,
    communityViewModel: CommunityViewModel
) {
    val selectedIndex by navigationViewModel.selectedIndex.collectAsState()

    val communities by communityViewModel.communities.collectAsState()
    val tags = remember { mutableStateListOf<String>() } // Lista de etiquetas seleccionadas
    val predefinedTags = listOf("Oficial", "Deportes", "Arte", "Cultura", "Tecnología", "Educación") // Etiquetas predefinidas


    Scaffold (
        modifier = modifier,
        topBar = {
            Header(title = "Comunidades UCC")
        },
        bottomBar = {
            BottomNavBar(
                navController = navController,
                navItems = navItems,
                selectedIndex = selectedIndex,
                onItemSelected = { navigationViewModel.selectIndex(it) }
            )
        },
        floatingActionButton = {
            //FAB personalizado
            FloatingActionButtonCustom(
                onFabClick = {
                    navController.navigate("createPage")
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(0.dp)
                .fillMaxSize()
        ) {
            // Filtro por etiquetas
            Text(
                text = "Filtrar",
                style = Typography.titleSmall,
                modifier = Modifier.padding(bottom = 8.dp, top = 16.dp, start = 16.dp),
                color = DarkBlue
            )
            LazyRow(
                modifier = Modifier
                    .padding(start = 16.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(predefinedTags) { tag ->
                    FilterChip(
                        selected = tags.contains(tag),
                        onClick = {
                            if (tags.contains(tag)) {
                                tags.remove(tag)
                            } else {
                                tags.add(tag)
                            }
                        },
                        label = {
                            Text(
                                text = tag,
                                style = Typography.bodySmall,
                                color = if (tags.contains(tag)) Color.White else LightBlue
                            )
                        },
                        leadingIcon = if (tags.contains(tag)) {
                            {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Selected",
                                    tint = Color.White
                                )
                            }
                        } else null,
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = LightBlue,
                            selectedLabelColor = Color.White
                        ),
                        border = BorderStroke(1.dp, LightBlue)
                    )
                }
            }


            Spacer(modifier = Modifier.height(16.dp))

            // Lista de comunidades filtradas
            LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                items(communities.filter { community ->
                    if (tags.isEmpty()) {
                        true // Mostrar todas las comunidades si no hay filtros seleccionados
                    } else {
                        tags.any { tag ->
                            when (tag) {
                                "Oficial" -> community.isOfficial // Filtrar por comunidades oficiales
                                else -> community.tags.contains(tag) // Filtrar por las etiquetas estándar
                            }
                        }
                    }
                }) { community ->
                    val isFollowed = communityViewModel.followedCommunities.collectAsState().value.contains(community.name)
                    CommunityCard(
                        community = community,
                        isFollowed = isFollowed,
                        onFollowClick = { communityName -> communityViewModel.toggleCommunityFollow(communityName) },
                        onClick = { navController.navigate("communityDetail/${community.id}") }
                    )
                }
            }

        }
    }
}
