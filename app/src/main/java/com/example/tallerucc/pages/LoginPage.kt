package com.example.tallerucc.pages

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.tallerucc.pages.composables.BottomSection
import com.example.tallerucc.pages.composables.TopSection
import com.example.tallerucc.ui.theme.DarkBlue
import com.example.tallerucc.ui.theme.LightBlue
import com.example.tallerucc.viewModel.AuthState
import com.example.tallerucc.viewModel.AuthViewModel

@Composable
fun LoginPage(
    modifier: Modifier = Modifier,
    navController: NavController,
    authViewModel: AuthViewModel
    ) {

    var email by remember {
        mutableStateOf("")
    }
    var password by remember {
        mutableStateOf("")
    }
    val authState = authViewModel.authState.observeAsState()
    val context = LocalContext.current

    LaunchedEffect(authState.value) {
        when (authState.value) {
            is AuthState.Authenticated -> navController.navigate("home")
            is AuthState.Error -> Toast.makeText(
                context,
                (authState.value as AuthState.Error).message, Toast.LENGTH_SHORT
            ).show()

            else -> Unit
        }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .background(
                brush = Brush.horizontalGradient(
                    colors = listOf(DarkBlue, LightBlue),
                    startX = 0f,
                    endX = Float.POSITIVE_INFINITY
                )
            ),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TopSection(
            modifier = Modifier,
            "Inicia Sesión",
            "Escribe tu email y contraseña"
        )



        BottomSection(
            modifier = Modifier,
            navController = navController,

            //values
            email = email,
            onEmailChanged = { email = it },
            password = password,
            onPasswordChanged = { password = it },

            //Text
            labelEmailText = "Email",
            labelPasswordText = "Contraseña",
            buttonLoginText = "Iniciar",

            //Click events
            authState = authViewModel.authState.observeAsState(),
            onLoginClick = {authViewModel.login(email, password)},
            onForgotPasswordClick = { authViewModel.forgotPassword(email) },
            onRegisterClick = {navController.navigate("signup")},
            onGoogleLoginClick = {},

            //Booleans
            showEmailField = true,
            showPasswordField = true,
            showForgotPassword = true,
            showLoginButton = true,
            showRegisterOption = true,
            showGoogleLoginButton = false
        )

        if (authState.value is AuthState.Loading) {
            CircularProgressIndicator(modifier = Modifier.padding(16.dp))
        }

        if (authState.value is AuthState.Error) {
            Text(
                text = (authState.value as AuthState.Error).message,
                color = Color.Red,
                modifier = Modifier.padding(16.dp)
            )
        }


    }
}