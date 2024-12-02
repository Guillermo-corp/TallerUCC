package com.example.tallerucc

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.tallerucc.pages.*
import com.example.tallerucc.navigation.navItems
import com.example.tallerucc.pages.workshops.WorkshopDetailPage
import com.example.tallerucc.pages.workshops.WorkshopsByCategoryPage
import com.example.tallerucc.pages.workshops.WorkshopsCategoriesPage
import com.example.tallerucc.viewModel.AuthViewModel
import com.example.tallerucc.viewModel.CommunityViewModel
import com.example.tallerucc.viewModel.CreateViewModel
import com.example.tallerucc.viewModel.HomeViewModel
import com.example.tallerucc.viewModel.NavigationViewModel
import com.example.tallerucc.viewModel.NotificationViewModel
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun MyAppNavigation(
    modifier: Modifier = Modifier,
    authViewModel: AuthViewModel,
    navController: androidx.navigation.NavHostController, // Asegurarse de que sea NavHostController
    navigationViewModel: NavigationViewModel = viewModel(),
    homeViewModel: HomeViewModel = viewModel(),

    ) {

    val selectedIndex by navigationViewModel.selectedIndex.collectAsState()
    val notificationViewModel: NotificationViewModel = viewModel()


    // Observa los cambios en la navegación
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // Llama al ViewModel para actualizar el índice según la ruta actual
    LaunchedEffect(currentRoute) {
        navigationViewModel.updateSelectedIndexBasedOnRoute(currentRoute)
        notificationViewModel.loadUnreadNotificationsCount()
    }

    NavHost(navController = navController, startDestination = "login") {
        composable("login") {
            LoginPage(navController = navController, authViewModel = authViewModel)
        }
        composable("signup") {
            SignupPage(navController = navController, authViewModel = authViewModel)
        }
        composable("verificationPending") {
            VerificationPendingPage(navController = navController, authViewModel = authViewModel)
        }
        composable("home") {
            HomePage(navController = navController, homeViewModel = homeViewModel, navigationViewModel = navigationViewModel, authViewModel = AuthViewModel(), notificationViewModel = NotificationViewModel())
        }
        composable("communities") {
            val communityViewModel: CommunityViewModel = viewModel() // Use viewModel()
            CommunitiesPage(
                navController = navController,
                communityViewModel = communityViewModel,
                navigationViewModel = navigationViewModel,
                authViewModel = AuthViewModel(),
                notificationViewModel = NotificationViewModel()
            )
        }
        composable("communityDetail/{communityId}") { backStackEntry ->
            val communityId = backStackEntry.arguments?.getString("communityId")
            if (communityId != null) {
                val communityViewModel: CommunityViewModel = viewModel() // Usa el ViewModel existente
                CommunityDetailsPage(
                    navController = navController,
                    communityId = communityId,
                    communityViewModel = communityViewModel,
                    navigationViewModel = navigationViewModel,
                    notificationViewModel = NotificationViewModel()
                )
            }
        }

        composable("workshops") {
            WorkshopsCategoriesPage(
                navController = navController,
                navigationViewModel = navigationViewModel,
                authViewModel = AuthViewModel(),
                notificationViewModel = NotificationViewModel()
            )
        }
        composable("createPage") {
            CreatePage(
                navController = navController,
                createViewModel = CreateViewModel(),
                authViewModel = AuthViewModel()
            )
        }
        composable("workshops/{categoryId}") { backStackEntry ->
            val categoryId = backStackEntry.arguments?.getString("categoryId")
            if (categoryId != null) {
                val categoryReference = FirebaseFirestore.getInstance().collection("categories").document(categoryId)
                WorkshopsByCategoryPage(
                    navController = navController,
                    categoryReference = categoryReference,
                    navigationViewModel = navigationViewModel,
                    authViewModel = AuthViewModel(),
                    notificationViewModel = NotificationViewModel()
                )
            }
        }

        composable("workshopDetail/{workshopId}") { backStackEntry ->
            val workshopId = backStackEntry.arguments?.getString("workshopId")
            if (workshopId != null) {
                val workshopReference = FirebaseFirestore.getInstance().collection("workshops").document(workshopId)
                WorkshopDetailPage(
                    navController = navController,
                    workshopReference = workshopReference,
                    navigationViewModel = navigationViewModel,
                    authViewModel = AuthViewModel(),
                    notificationViewModel = NotificationViewModel()
                )
            }
        }

        composable("notifications") {
            NotificationPage(
                navController = navController,
                notificationViewModel = notificationViewModel,
                navigationViewModel = navigationViewModel,
                authViewModel = AuthViewModel()
            )
        }

    }
}



