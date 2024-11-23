package com.example.tallerucc.pages.composables

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults.buttonColors
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.focusModifier
import androidx.compose.ui.focus.focusTarget
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.tallerucc.R
import com.example.tallerucc.ui.theme.Black
import com.example.tallerucc.ui.theme.DarkBlue
import com.example.tallerucc.ui.theme.DarkGrey
import com.example.tallerucc.ui.theme.LightBlue
import com.example.tallerucc.ui.theme.LightGrey
import com.example.tallerucc.ui.theme.Poppins
import com.example.tallerucc.ui.theme.Typography
import com.example.tallerucc.ui.theme.White
import com.example.tallerucc.viewModel.AuthState
import com.example.tallerucc.viewModel.AuthViewModel

@Composable
fun TopSection(
    modifier: Modifier = Modifier,
    title : String,
    subtitle : String,
    logoResId : Int = R.drawable.logo_white
) {
    Column (
        modifier = modifier
            .fillMaxWidth()
            .padding(24.dp)
            .padding(top = 32.dp),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.Center,
    ){
        Image(
            painter = painterResource(id = logoResId),
            contentDescription = "Logo",
            modifier = Modifier
                .padding(vertical = 16.dp)
                .height(105.dp)

        )
        Text(
            text = title,
            style = Typography.titleLarge,
            color = White,
            modifier = Modifier
                .padding(vertical = 8.dp)

        )
        Text(
            text = subtitle,
            fontFamily = Poppins,
            style = Typography.titleSmall,
            color = White,
            modifier = Modifier
                .padding(vertical = 8.dp)
        )

    }

}


@Composable
fun BottomSection(
    modifier: Modifier = Modifier,
    navController: NavController,
    //val
    email: String, // Add email parameter
    onEmailChanged: (String) -> Unit, // Add onEmailChanged callback
    password: String, // Addpassword parameter
    onPasswordChanged: (String) -> Unit, // Add onPasswordChanged callback

    //Parameters for text
    labelEmailText: String,
    labelPasswordText: String,
    buttonLoginText: String,

    //Parameters for click events
    authState: State<AuthState?>,
    onLoginClick: () -> Unit,
    onForgotPasswordClick: () -> Unit,
    onRegisterClick: () -> Unit,
    onGoogleLoginClick: () -> Unit,

    //Parameter for visibility
    showEmailField: Boolean = true, // Add showEmailField parameter
    showPasswordField: Boolean = true, // Add showPasswordField parameter
    showForgotPassword: Boolean = true, // Add showForgotPassword parameter
    showLoginButton: Boolean = true, // Add showRegisterButton parameter
    showRegisterOption: Boolean = true, // Add showRegisterOption parameter
    showGoogleLoginButton: Boolean = true // Add showGoogleLoginButton parameter
) {

    val focusManager = LocalFocusManager.current

    Column(
        modifier = modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
            .background(color = White)
            .padding(24.dp)
            .padding(top = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Email field
        if (showEmailField) {

            Text(
                text = labelEmailText,
                style = Typography.titleSmall,
                color = DarkBlue,
                modifier = Modifier
                    .align(Alignment.Start)
                    .padding(bottom = 8.dp)
            )

            OutlinedTextField(
                value = email,
                onValueChange = onEmailChanged,
                modifier = Modifier
                    .fillMaxWidth()
                    .focusable()
                    .onKeyEvent {
                        if (it.key == Key.Enter && it.type == KeyEventType.KeyDown) {
                            focusManager.moveFocus(FocusDirection.Next)
                            true // Consume the event
                        } else {
                            false // Don't consume the event
                        }
                    },
                shape = RoundedCornerShape(8.dp),
                colors = OutlinedTextFieldDefaults
                    .colors(
                        focusedBorderColor = DarkBlue,
                        unfocusedBorderColor = LightGrey
                    )
            )
        }

        // Password field
        if (showPasswordField) {

            Text(
                text = labelPasswordText,
                style = Typography.titleSmall,
                color = DarkBlue,
                modifier = Modifier
                    .align(Alignment.Start)
                    .padding(top = 16.dp)
            )

            OutlinedTextField(
                value = password,
                onValueChange = onPasswordChanged,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
                    .focusable()
                    .onKeyEvent {
                        if (it.key == Key.Enter && it.type == KeyEventType.KeyDown) {
                            focusManager.moveFocus(FocusDirection.Next)
                            true // Consume the event
                        } else {
                            false // Don't consume the event
                        }
                    },
                shape = RoundedCornerShape(8.dp),
                colors = OutlinedTextFieldDefaults
                    .colors(
                        focusedBorderColor = DarkBlue,
                        unfocusedBorderColor = LightGrey
                    ),
                visualTransformation = PasswordVisualTransformation()
            )
        }

        // Forgot password
        if (showForgotPassword) {
            Text(
                text = "¿Olvidaste tu contraseña?",
                style = Typography.labelSmall,
                color = LightBlue,
                modifier = Modifier
                    .align(Alignment.Start)
                    .padding(vertical = 16.dp)
                    .clickable { onForgotPasswordClick() },
            )
        }

        // Login button
        if (showLoginButton) {
            Button(
                onClick = onLoginClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
                    .shadow(
                        elevation = 4.dp,
                        shape = RoundedCornerShape(100.dp),
                        clip = true,
                        ambientColor = DarkGrey,
                        spotColor = DarkGrey
                    ),
                colors = buttonColors(containerColor = DarkBlue),
                enabled = authState.value !is AuthState.Loading
            ) {
                Text(buttonLoginText, style = Typography.labelMedium    )
            }

            // Register option
            if (showRegisterOption) {
                Row(
                    horizontalArrangement = Arrangement.Start,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp)
                ) {
                    Text(
                        text = "¿Todavía no tienes una cuenta?  ",
                        style = Typography.bodySmall,
                        color = DarkGrey
                    )
                    Text(
                        text = "Regístrate",
                        style = Typography.labelSmall,
                        color = LightBlue,
                        modifier = Modifier.clickable { onRegisterClick() }
                    )
                }
            }

            // Google login button
            if (showGoogleLoginButton) {

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    HorizontalDivider(
                        modifier = Modifier.weight(1f),
                        color = LightGrey // Or your desired color
                    )
                    Text(
                        text = "   O   ", // Or your desired text
                        style = Typography.bodyMedium, // Or your desired style
                        color = DarkGrey // Or your desired color
                    )
                    HorizontalDivider(
                        modifier = Modifier.weight(1f),
                        color = LightGrey // Or your desired color
                    )
                }


                OutlinedButton(
                    onClick = onGoogleLoginClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, DarkBlue),
                    enabled = authState.value !is AuthState.Loading
                ) {
                    Text(
                        text = "Continuar con Google",
                        style = Typography.labelSmall,
                        color = DarkBlue
                    )
                }
            }
        }
    }
}



/*@Preview
@Composable
fun TopSectionPreview(
    modifier: Modifier = Modifier
) {
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
    ){
        TopSection(
            modifier = Modifier,
            "Bienvenido",
            "Escribe tu email y contraseña para poder continuar"
        )
        val authViewModel = AuthViewModel()
        val authState = authViewModel.authState.observeAsState()
        BottomSection(
            modifier = Modifier,
            navController = NavController,
            "Email",
            {""},
            "Iniciar",
            onPasswordChanged = {},
            labelEmailText = "Email",
            labelPasswordText = "Contraseña",
            buttonLoginText = "Iniciar",

            authState = authState,
            onLoginClick = {},
            onForgotPasswordClick = {},
            onRegisterClick = {},
            onGoogleLoginClick = {},
            showEmailField = true,
            showPasswordField = true,
            showForgotPassword = true,
            showLoginButton = true,
            showRegisterOption = true,
            showGoogleLoginButton = true
        )
    }

}*/
