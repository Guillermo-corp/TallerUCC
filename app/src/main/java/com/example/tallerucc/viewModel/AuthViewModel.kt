package com.example.tallerucc.viewModel

import android.util.Log
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.navigation.NavHostController
import com.example.tallerucc.utils.NotificationHelper.updateDeviceToken
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging


class AuthViewModel : ViewModel() {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    private val _authState = MutableLiveData<AuthState>()
    val authState: LiveData<AuthState> = _authState

    init {
        auth.addAuthStateListener { firebaseAuth ->
            val state = if (firebaseAuth.currentUser == null) "Unauthenticated" else "Authenticated"
            Log.d("AuthViewModel", "AuthState changed: $state")
            _authState.value = if (firebaseAuth.currentUser == null) AuthState.Unauthenticated else AuthState.Authenticated
        }
    }


    fun checkAuthState() {
        if (auth.currentUser != null) {
            _authState.value = AuthState.Authenticated
        } else {
            _authState.value = AuthState.Unauthenticated
        }
    }


    fun checkVerificationStatus() {
        auth.currentUser?.reload()?.addOnCompleteListener { reloadTask ->
            if (reloadTask.isSuccessful) {
                if (auth.currentUser?.isEmailVerified == true) {
                    _authState.value = AuthState.Authenticated
//                    navController.navigate("home") // Navigate to home if verified
                } else {
                    // Show a message to the user that verification is still pending
                    _authState.value = AuthState.Error("Verificación de correo electrónico todavía pendiente.")
                }
            } else {
                // Handle error
            }
        }
    }

    fun login(email: String, password: String) {
        if (email.isEmpty() || password.isEmpty()) {
            val errorMessage = "Email and password can't be empty"
            Log.e("AuthViewModel", errorMessage)
            _authState.value = AuthState.Error(errorMessage)
            return
        }

        _authState.value = AuthState.Loading
        Log.d("AuthViewModel", "Login initiated for email: $email")

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    if (auth.currentUser?.isEmailVerified == true) {
                        Log.d("AuthViewModel", "Login successful for email: $email")

                        _authState.value = AuthState.Authenticated
                        // Actualizar el token del dispositivo
                        checkAndUpdateDeviceToken()

                    } else {
                        val errorMessage = "Please verify your email before logging in."
                        Log.e("AuthViewModel", errorMessage)
                        _authState.value = AuthState.Error(errorMessage)
                    }
                } else {
                    val errorMessage = handleAuthError(task.exception)
                    Log.e("AuthViewModel", "Login failed: $errorMessage", task.exception)
                    _authState.value = AuthState.Error(errorMessage)
                }
            }
    }




    fun signup(email: String, password: String) {
        if (email.isEmpty() || password.isEmpty()) {
            val errorMessage = "Email and password can't be empty"
            Log.e("AuthViewModel", errorMessage)
            _authState.value = AuthState.Error(errorMessage)
            return
        }

        _authState.value = AuthState.Loading
        Log.d("AuthViewModel", "Signup initiated for email: $email")

        // Set the language before creating the user
        auth.setLanguageCode("es")

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val userId = task.result?.user?.uid ?: return@addOnCompleteListener
                    Log.d("AuthViewModel", "Signup successful for email: $email (userId: $userId)")

                    // Send verification email
                    auth.currentUser?.sendEmailVerification()
                        ?.addOnCompleteListener { verificationTask ->
                            if (verificationTask.isSuccessful) {
                                Log.d("AuthViewModel", "Verification email sent to $email")

                                // Create user document in Firestore
                                val userData = mapOf(
                                    "email" to email,
                                    "roles" to listOf("usuario"),
                                    "followedCommunities" to emptyList<String>(),
                                    "communitiesCreated" to emptyList<String>(),
                                    "registeredWorkshops" to emptyList<String>(),// Default role
                                    "likedPosts" to emptyList<String>(),
                                    "deviceToken" to emptyList<String>()
                                )

                                FirebaseFirestore.getInstance()
                                    .collection("users")
                                    .document(userId)
                                    .set(userData)
                                    .addOnSuccessListener {
                                        Log.d("AuthViewModel", "User document created in Firestore for $email (userId: $userId)")
                                        _authState.value = AuthState.VerificationPending
                                    }
                                    .addOnFailureListener { e ->
                                        val errorMessage = "User registered but failed to create Firestore document: ${e.message}"
                                        Log.e("AuthViewModel", errorMessage, e)
                                        _authState.value = AuthState.Error(errorMessage)
                                    }
                            } else {
                                val errorMessage = handleAuthError(verificationTask.exception)
                                Log.e("AuthViewModel", "Failed to send verification email: $errorMessage", verificationTask.exception)
                                _authState.value = AuthState.Error(errorMessage)
                            }
                        }
                } else {
                    val errorMessage = handleAuthError(task.exception) // Centralización de errores
                    Log.e("AuthViewModel", "Signup failed: $errorMessage", task.exception) // Log error
                    _authState.value = AuthState.Error(errorMessage)
                }
            }
    }


    fun signout() {
        Log.d("AuthViewModel", "Signout called")
        auth.signOut()
        checkAuthState()
    }


    fun forgotPassword(email: String) {
        // Check if the email is empty
        if (email.isEmpty()) {
            _authState.value = AuthState.Error("Email can't be empty")
            return
        }
        _authState.value = AuthState.Loading

        // Call Firebase's sendPasswordResetEmail function
        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    _authState.value = AuthState.Error("Password reset email sent")
                } else {
                    _authState.value = AuthState.Error(task.exception?.message ?: "Unknown error")
                }
            }
    }

    fun checkAndUpdateDeviceToken() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val token = task.result
                if (token != null) {
                    updateDeviceToken(token)
                    Log.d("AuthViewModel", "Token actualizado correctamente.")
                } else {
                    Log.e("AuthViewModel", "Error al obtener el token del dispositivo")
                }
            } else {
                Log.e("AuthViewModel", "Error al completar la tarea del token", task.exception)
            }
        }
    }



}

sealed class AuthState {
    object Authenticated : AuthState()
    object Unauthenticated : AuthState()
    object VerificationPending : AuthState()
    object Loading : AuthState()
    data class Error(val message: String) : AuthState()
}

private fun handleAuthError(exception: Exception?): String {
    return when (exception) {
        is FirebaseAuthInvalidUserException -> "No account found with this email."
        else -> exception?.message ?: "An unknown error occurred."
    }
}
