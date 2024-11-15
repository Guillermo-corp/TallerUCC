package com.example.tallerucc

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.tallerucc.pages.HomePage
import com.example.tallerucc.pages.LoginPage
import com.example.tallerucc.pages.SignupPage
import com.example.tallerucc.pages.VerificationPendingPage
import com.example.tallerucc.viewModel.AuthViewModel


@Composable
fun MyAppNavigation(modifier: Modifier = Modifier, authViewModel: AuthViewModel) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "login", builder = {
        composable("login"){
            LoginPage(navController = navController, authViewModel = authViewModel)
        }
        composable("signup"){
            SignupPage(navController = navController, authViewModel = authViewModel)
        }
        composable("home"){
            HomePage(navController = navController, authViewModel = authViewModel)
        }
        composable("verificationPending") {
            VerificationPendingPage(navController = navController, authViewModel = authViewModel)
        }
    })
}
