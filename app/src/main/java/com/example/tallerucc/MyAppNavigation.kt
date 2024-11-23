package com.example.tallerucc

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
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
import com.example.tallerucc.viewModel.NavigationViewModel
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun MyAppNavigation(
    modifier: Modifier = Modifier,
    authViewModel: AuthViewModel,
    navigationViewModel: NavigationViewModel = viewModel()
) {
    val navController = rememberNavController()
    val selectedIndex by navigationViewModel.selectedIndex.collectAsState()

    // Observa los cambios en la navegación
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // Llama al ViewModel para actualizar el índice según la ruta actual
    LaunchedEffect(currentRoute) {
        navigationViewModel.updateSelectedIndexBasedOnRoute(currentRoute)
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
            HomePage(navController = navController, authViewModel = authViewModel, navigationViewModel = navigationViewModel)
        }
        composable("communities") {
            CommunitiesPage(navController = navController, authViewModel = authViewModel, navigationViewModel = navigationViewModel)
        }
        composable("workshops") {
            WorkshopsCategoriesPage(
                navController = navController,
                navigationViewModel = navigationViewModel
            )
        }
        composable("workshops/{categoryId}") { backStackEntry ->
            val categoryId = backStackEntry.arguments?.getString("categoryId")
            if (categoryId != null) {
                val categoryReference = FirebaseFirestore.getInstance().collection("categories").document(categoryId)
                WorkshopsByCategoryPage(
                    navController = navController,
                    categoryReference = categoryReference,
                    navigationViewModel = navigationViewModel
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
                    navigationViewModel = navigationViewModel
                )
            }
        }

        composable("notifications") {
            NotificationPage(navController = navController, authViewModel = authViewModel, navigationViewModel = navigationViewModel)
        }
    }
}



