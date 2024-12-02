package com.example.tallerucc.pages

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.tallerucc.navigation.navItems
import com.example.tallerucc.pages.composables.BottomNavBar
import com.example.tallerucc.pages.composables.FloatingActionButtonCustom
import com.example.tallerucc.pages.composables.Header
import com.example.tallerucc.pages.composables.PostCard
import com.example.tallerucc.ui.theme.Typography
import com.example.tallerucc.viewModel.AuthState
import com.example.tallerucc.viewModel.AuthViewModel
import com.example.tallerucc.viewModel.HomeViewModel
import com.example.tallerucc.viewModel.NavigationViewModel
import com.google.firebase.auth.FirebaseAuth

import androidx.compose.foundation.lazy.rememberLazyListState

@Composable
fun HomePage(
    modifier: Modifier = Modifier,
    navController: NavController,
    homeViewModel: HomeViewModel,
    navigationViewModel: NavigationViewModel,
    authViewModel: AuthViewModel
) {
    val selectedIndex by navigationViewModel.selectedIndex.collectAsState()
    val posts by homeViewModel.posts.collectAsState()
    val lazyListState = rememberLazyListState() // Recordar el estado del LazyColumn
    val authState = authViewModel.authState.observeAsState()

    LaunchedEffect(authState.value) {
        when (authState.value) {
            is AuthState.Unauthenticated -> {
                navController.navigate("login") {
                    popUpTo(0) // Limpia toda la pila de navegación
                }
            }
            else -> Unit
        }
    }

    LaunchedEffect(authState.value) {
        if (authState.value is AuthState.Authenticated) {
            homeViewModel.loadPostsForFeed()
            homeViewModel.loadLikedPosts()
            homeViewModel.loadCommunities()
        }
    }


    // Detectar cuando el índice visible está cerca de cargar nuevos elementos
    LaunchedEffect(lazyListState.firstVisibleItemIndex) {
        val prefetchThreshold = 5 // Número de elementos para precargar
        val lastIndex = posts.size - 1

        if (lazyListState.firstVisibleItemIndex + prefetchThreshold >= lastIndex) {
            // Precarga lógica aquí si usas paginación o datos adicionales
        }
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            Header(
                title = "Tu Taller UCC",
                showBackIcon = true,
                onBackClick = { navController.popBackStack() }, // Navegar hacia atrás
                showLogoutIcon = true,
                onLogoutClick = {
                    authViewModel.signout() // Cerrar sesión
                }
            )
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
            FloatingActionButtonCustom(
                onFabClick = {
                    navController.navigate("createPage")
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 0.dp)
        ) {
            if (posts.isEmpty()) {
                Text(
                    text = "No hay publicaciones disponibles.",
                    style = Typography.bodyMedium,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            } else {
                LazyColumn(
                    state = lazyListState, // Asociar el estado
                    verticalArrangement = Arrangement.spacedBy(0.dp)
                ) {
                    items(posts) { post ->
                        val postId = post["postId"] as? String ?: return@items
                        val isLiked = homeViewModel.likedPosts.collectAsState().value.contains(postId)
                        val likesCount = (post["likesCount"] as? Long) ?: 0

                        val isCommunityOfficial = post["communityName"]?.let { communityName ->
                            homeViewModel.isCommunityOfficial(communityName.toString())
                        } ?: false

                        PostCard(
                            post = post,
                            userId = FirebaseAuth.getInstance().currentUser?.uid ?: "",
                            isLiked = isLiked,
                            isCommunityOfficial = isCommunityOfficial,// Indicador adicional para comunidades oficiales
                            onLikeToggle = { postId, isNowLiked ->
                                homeViewModel.toggleLike(postId, isNowLiked)
                            }
                        )
                    }
                }
            }
        }
    }
}

