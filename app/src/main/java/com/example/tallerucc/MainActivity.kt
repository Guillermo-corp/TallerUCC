package com.example.tallerucc

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.tallerucc.ui.theme.TallerUCCTheme
import com.example.tallerucc.utils.NotificationHelper
import com.example.tallerucc.viewModel.AuthViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val authViewModel: AuthViewModel by viewModels()
        setContent {
            // Verifica el estado de autenticación
            LaunchedEffect(Unit) {
                authViewModel.checkAuthState()
            }
            TallerUCCTheme {

                    MyAppNavigation(modifier = Modifier.padding(),authViewModel = authViewModel)

            }
        }

        // Generate the device token for debugging purposes only
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val token = task.result
                Log.d("FCM", "Device token: $token")
            } else {
                Log.e("FCM", "Failed to retrieve device token", task.exception)
            }
        }
    }
}


