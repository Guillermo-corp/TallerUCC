package com.example.tallerucc.pages

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults.buttonColors
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.example.tallerucc.ui.theme.Black
import com.example.tallerucc.ui.theme.DarkBlue
import com.example.tallerucc.ui.theme.DarkGrey
import com.example.tallerucc.ui.theme.LightBlue
import com.example.tallerucc.viewModel.AuthState
import com.example.tallerucc.viewModel.AuthViewModel
import com.example.tallerucc.ui.theme.Typography
import com.example.tallerucc.ui.theme.White

@Composable
fun VerificationPendingPage(modifier: Modifier = Modifier, navController: NavController, authViewModel: AuthViewModel) {
    // UI elements to inform the user about email verification
    // and a button to check verification status

    val authState = authViewModel.authState.observeAsState()
    val context = LocalContext.current

    LaunchedEffect(authState.value) {
        when (authState.value) {
            is AuthState.Authenticated -> navController.navigate("home")
            is AuthState.Error -> Toast.makeText(context,
                (authState.value as AuthState.Error).message, Toast.LENGTH_SHORT).show()
            else -> Unit
        }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .background(
                color = White
            ),
            /*.background(
                brush = Brush.horizontalGradient(
                    colors = listOf(DarkBlue, LightBlue),
                    startX = 0f,
                    endX = Float.POSITIVE_INFINITY
                )
            ),*/
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Verificación Pendiente",
            style = Typography.titleLarge,
            color = DarkBlue,
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Por favor revisa tu email para verificar tu cuenta.",
            style = Typography.bodyMedium,
            color = DarkGrey,
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { authViewModel.checkVerificationStatus() },
            enabled = authState.value != AuthState.Loading,
            modifier = Modifier
                .padding(vertical = 8.dp)
                .shadow(
                    elevation = 4.dp,
                    shape = RoundedCornerShape(100.dp),
                    clip = true,
                    ambientColor = DarkGrey,
                    spotColor = DarkGrey
                ),
            colors = buttonColors(containerColor = DarkBlue),
            border = BorderStroke(0.dp, LightBlue)
        ) {
            Text(
                text = "Checar Verificación",
                style = Typography.labelMedium,
                color = White,
            )
        }
    }

}