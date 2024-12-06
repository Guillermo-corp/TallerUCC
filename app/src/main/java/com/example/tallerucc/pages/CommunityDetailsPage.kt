package com.example.tallerucc.pages

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.tallerucc.model.Community
import com.example.tallerucc.navigation.navItems
import com.example.tallerucc.pages.composables.BottomNavBar
import com.example.tallerucc.pages.composables.CommunityHeader
import com.example.tallerucc.pages.composables.PostCard
import com.example.tallerucc.utils.formatTimestamp
import com.example.tallerucc.viewModel.CommunityViewModel
import com.example.tallerucc.viewModel.NavigationViewModel
import com.example.tallerucc.viewModel.NotificationViewModel
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth

@Composable
fun CommunityDetailsPage(
    navController: NavController,
    communityId: String,
    communityViewModel: CommunityViewModel,
    navigationViewModel: NavigationViewModel,
    notificationViewModel: NotificationViewModel,
) {
    val community = remember { mutableStateOf<Community?>(null) }
    val posts = remember { mutableStateListOf<Map<String, Any>>() }
    val likedPosts = remember { mutableStateOf(listOf<String>()) }
    val currentUser = FirebaseAuth.getInstance().currentUser
    val selectedIndex by navigationViewModel.selectedIndex.collectAsState()

    // Cargar comunidades oficiales
    val communities by communityViewModel.communities.collectAsState()

    LaunchedEffect(communityId) {
        if (currentUser != null) {
            // Cargar los posts que el usuario ha dado like
            communityViewModel.loadLikedPosts(currentUser.uid) { liked ->
                likedPosts.value = liked
                println("Liked posts loaded for user ${currentUser.uid}: $liked")
            }

            // Cargar los detalles de la comunidad y sus publicaciones
            communityViewModel.getCommunityDetails(communityId) { loadedCommunity ->
                community.value = loadedCommunity
                if (loadedCommunity != null) {
                    println("Community loaded: ${loadedCommunity.name}")
                    communityViewModel.getCommunityPosts(loadedCommunity.name) { loadedPosts ->
                        posts.clear()
                        posts.addAll(loadedPosts)
                        println("Posts loaded for community ${loadedCommunity.name}: $loadedPosts")
                    }
                } else {
                    println("Failed to load community details for ID: $communityId")
                }
            }
        } else {
            println("No current user found!")
        }
    }

    // Verificar si una comunidad es oficial
    fun isCommunityOfficial(communityName: String?): Boolean {
        return communities.find { it.name == communityName }?.isOfficial ?: false
    }

    Scaffold(
        bottomBar = {
            BottomNavBar(
                navController = navController,
                navItems = navItems,
                selectedIndex = selectedIndex,
                onItemSelected = { navigationViewModel.selectIndex(it) },
                unreadNotificationsCount = notificationViewModel.unreadNotificationsCount.collectAsState().value
            )
        },
        contentWindowInsets = WindowInsets(0) // Deshabilitar los insets
    ) { innerPadding ->
        if (community.value == null) {
            Box(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(0.dp)
            ) {
                // Header de la comunidad
                item {
                    CommunityHeader(community = community.value!!)
                }

                // Publicaciones de la comunidad
                items(posts) { post ->
                    val postId = post["postId"] as? String ?: return@items
                    val isLiked = likedPosts.value.contains(postId)
                    println("Rendering postId: $postId, IsLiked: $isLiked")

                    val isCommunityOfficial = post["communityName"]?.let { communityName ->
                        isCommunityOfficial(communityName.toString())
                    } ?: false

                    val createdAt = post["createdAt"] as? Timestamp

                    // Formatear la fecha
                    val formattedDate = formatTimestamp(createdAt)

                    PostCard(
                        post = post,
                        userId = currentUser?.uid ?: "",
                        isLiked = isLiked,
                        isCommunityOfficial = isCommunityOfficial,
                        createdAt = formattedDate, // Pasa la fecha formateada
                    ) { postId, isNowLiked ->
                        if (currentUser != null) {
                            communityViewModel.toggleLike(postId, currentUser.uid, isNowLiked)
                            // Actualizar el estado local
                            likedPosts.value = if (isNowLiked) {
                                likedPosts.value + postId
                            } else {
                                likedPosts.value - postId
                            }
                        }
                    }
                }
            }
        }
    }

}

