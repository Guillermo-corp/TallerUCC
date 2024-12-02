package com.example.tallerucc.viewModel

import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tallerucc.R
import com.example.tallerucc.model.Category
import com.example.tallerucc.model.Community
import com.example.tallerucc.repository.uploadImageToImgur
import com.example.tallerucc.utils.Constants
import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.tasks.await
import com.google.firebase.firestore.FieldValue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.FileInputStream
import java.io.IOException

class CreateViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()
    private val client = OkHttpClient()


    // Usa el token directamente desde las constantes
    private val accessToken = Constants.ACCESS_TOKEN

    private val _userRoles = MutableStateFlow<List<String>>(emptyList())
    val userRoles: StateFlow<List<String>> = _userRoles

    private val _followedCommunities = MutableStateFlow<List<Community>>(emptyList())
    val followedCommunities: StateFlow<List<Community>> = _followedCommunities

    init {
        loadUserRoles()
        loadFollowedCommunities()
    }

    fun uploadImage(
        imageUri: Uri,
        context: Context,
        onComplete: (String?) -> Unit
    ) {
        uploadImageToImgur(imageUri, context, accessToken, onComplete)
    }


    private fun loadUserRoles() {
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            val userId = currentUser.uid
            db.collection("users").document(userId)
                .get()
                .addOnSuccessListener { document ->
                    val roles = document.get("roles") as? List<String> ?: emptyList()
                    _userRoles.value = roles
                    Log.d("CreateViewModel", "Loaded roles: $roles")
                }
                .addOnFailureListener { e ->
                    Log.e("CreateViewModel", "Error loading user roles: ${e.message}")
                }
        } else {
            Log.e("CreateViewModel", "User not logged in.")
        }
    }


    // Flow para observar las categorías
    val categories: StateFlow<List<Category>> = flow {
        val categoryList = mutableListOf<Category>()
        val snapshot = db.collection("categories").get().await()
        snapshot.documents.forEach { doc ->
            categoryList.add(
                Category(
                    id = doc.id,
                    name = doc.getString("name") ?: "",
                    reference = doc.reference
                )
            )
        }
        emit(categoryList)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())


    fun createWorkshop(
        name: String,
        description: String,
        imageUrls: List<String>,
        categoryReference: DocumentReference,
        schedule: List<Map<String, Any>>
    ) {
        val workshop = mapOf(
            "name" to name,
            "description" to description,
            "imageUrls" to imageUrls, // Guarda el array de URLs
            "categoryId" to categoryReference,
            "schedule" to schedule,
            "createdAt" to Timestamp.now()
        )
        db.collection("workshops").add(workshop)
            .addOnSuccessListener { Log.d("CreatePage", "Workshop created successfully!") }
            .addOnFailureListener { e -> Log.e("CreatePage", "Error creating workshop", e) }
    }

    fun createCommunity(
        name: String,
        description: String,
        iconUrl: String,
        bannerUrl: String,
        tags: List<String>,
        isOfficial: Boolean, // Nuevo parámetro
        userEmail: String, // Email del usuario autenticado
        userId: String // UID del usuario autenticado
    ) {
        val community = mapOf(
            "name" to name,
            "description" to description,
            "iconUrl" to iconUrl,
            "bannerUrl" to bannerUrl,
            "tags" to tags, // Incluimos las etiquetas
            "postsCount" to 0, // Inicializamos el contador de publicaciones
            "members" to listOf(userEmail), // Lista inicial de miembros con el creador
            "createdBy" to userEmail, // Email del creador
            "createdAt" to Timestamp.now(),
            "isOfficial" to isOfficial, // Nuevo campo para indicar si es oficial
        )

        db.collection("communities").add(community)
            .addOnSuccessListener { communityRef ->
                Log.d("CreateCommunity", "Comunidad creada exitosamente: ${communityRef.id}")

                // Actualizar el array `communitiesCreated` del usuario
                val userDocRef = db.collection("users").document(userId)
                userDocRef.update(
                    "communitiesCreated", FieldValue.arrayUnion(name),
                    "followedCommunities", FieldValue.arrayUnion(name)
                )
                    .addOnSuccessListener {
                        Log.d("CreateCommunity", "Comunidad añadida a 'communitiesCreated' y 'followedCommunities' del usuario: $userId")
                    }
                    .addOnFailureListener { e ->
                        Log.e("CreateCommunity", "Error actualizando las comunidades del usuario.", e)
                    }
            }
            .addOnFailureListener { e ->
                Log.e("CreateCommunity", "Error creando la comunidad.", e)
            }
    }

    fun isCommunityNameAvailable(name: String, onResult: (Boolean) -> Unit) {
        db.collection("communities")
            .whereEqualTo("name", name)
            .get()
            .addOnSuccessListener { querySnapshot ->
                // Si no hay documentos, el nombre está disponible
                onResult(querySnapshot.isEmpty)
            }
            .addOnFailureListener { e ->
                Log.e("CreateCommunity", "Error checking community name availability.", e)
                onResult(false) // Asume que el nombre no está disponible en caso de error
            }
    }

    fun createPost(
        context: Context,
        title: String,
        textContent: String,
        authorId: String,
        communityName: String?,
        communityLogo: String?, // Nuevo parámetro
        imageUrls: List<String>, // Cambiado a List<String>
        isOfficial: Boolean,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                // Crear la publicación en Firestore
                val post = mapOf(
                    "title" to title,
                    "textContent" to textContent,
                    "authorId" to authorId,
                    "communityName" to communityName, // Null si es oficial
                    "communityLogo" to communityLogo, // Agregar logo al documento
                    "createdAt" to Timestamp.now(),
                    "likesCount" to 0,
                    "imageUrls" to imageUrls,
                    "isOfficial" to isOfficial
                )

                db.collection("posts")
                    .add(post)
                    .addOnSuccessListener {
                        Log.d("CreateViewModel", "Post created successfully")

                        // Enviar notificaciones dependiendo del tipo de publicación
                        if (isOfficial) {
                            sendNotificationToAllUsers(context, title, textContent)
                        } else if (communityName != null) {
                            sendNotificationToCommunityFollowers(context, communityName, title, textContent)
                        }

                        onSuccess()
                    }
                    .addOnFailureListener { exception ->
                        Log.e("CreateViewModel", "Error creating post", exception)
                        onFailure(exception)
                    }
            } catch (e: Exception) {
                Log.e("CreateViewModel", "Error al crear la publicación: ${e.message}", e)
                onFailure(e)
            }
        }
    }


    private fun sendNotificationToAllUsers(context: Context, title: String, message: String) {
        viewModelScope.launch {
            try {
                val querySnapshot = db.collection("users").get().await()

                querySnapshot.documents.forEach { document ->
                    val userId = document.id
                    val tokens = document.get("deviceTokens") as? List<String> // Cambiar a array

                    if (!tokens.isNullOrEmpty()) {
                        tokens.forEach { token ->
                            addNotificationToFirestore(userId, title, message)
                            sendPushNotificationToDevice(context, token, title, message) // Enviar a cada token
                        }
                    } else {
                        Log.e("CreateViewModel", "No tokens found for user $userId")
                    }
                }
            } catch (e: Exception) {
                Log.e("CreateViewModel", "Error fetching users", e)
            }
        }
    }




    private fun sendNotificationToCommunityFollowers(context: Context, communityName: String, title: String, message: String) {
        viewModelScope.launch {
            try {
                val querySnapshot = db.collection("users")
                    .whereArrayContains("followedCommunities", communityName)
                    .get()
                    .await()

                querySnapshot.documents.forEach { document ->
                    val userId = document.id
                    val tokens = document.get("deviceTokens") as? List<String> // Cambiar a array

                    if (!tokens.isNullOrEmpty()) {
                        tokens.forEach { token ->
                            addNotificationToFirestore(userId, title, message)
                            sendPushNotificationToDevice(context, token, title, message) // Enviar a cada token
                        }
                    } else {
                        Log.e("CreateViewModel", "No tokens found for user $userId")
                    }
                }
            } catch (e: Exception) {
                Log.e("CreateViewModel", "Error fetching community followers", e)
            }
        }
    }




    suspend fun sendPushNotificationToDevice(context: Context, deviceToken: String, title: String, body: String) {
        withContext(Dispatchers.IO) {
            try {
                // Ruta al archivo JSON de la clave privada
                val googleCredentials = GoogleCredentials.fromStream(
                    context.resources.openRawResource(R.raw.service_account)
                ).createScoped(listOf("https://www.googleapis.com/auth/firebase.messaging"))

                googleCredentials.refreshIfExpired()
                val accessToken = googleCredentials.accessToken.tokenValue

                // Construir el payload
                val jsonPayload = """
                {
                    "message": {
                        "token": "$deviceToken",
                        "notification": {
                            "title": "$title",
                            "body": "$body"
                        }
                    }
                }
            """.trimIndent()

                // Enviar la solicitud
                val client = OkHttpClient()
                val mediaType = "application/json".toMediaTypeOrNull()
                val requestBody = jsonPayload.toRequestBody(mediaType)
                val request = Request.Builder()
                    .url("https://fcm.googleapis.com/v1/projects/tallerucc-51203/messages:send")
                    .post(requestBody)
                    .addHeader("Authorization", "Bearer $accessToken")
                    .addHeader("Content-Type", "application/json")
                    .build()

                client.newCall(request).execute().use { response ->
                    if (response.isSuccessful) {
                        Log.d("FCM", "Notificación enviada con éxito.")
                    } else {
                        Log.e("FCM", "Error al enviar notificación: ${response.body?.string()}")
                    }
                }
            } catch (e: Exception) {
                Log.e("FCM", "Error en sendPushNotificationToDevice: ${e.message}", e)
            }
        }
    }




    private fun addNotificationToFirestore(userId: String, title: String, message: String) {
        val notification = mapOf(
            "userId" to userId,
            "title" to title,
            "message" to message,
            "timestamp" to Timestamp.now(),
            "read" to false
        )

        db.collection("notifications")
            .add(notification)
            .addOnSuccessListener {
                Log.d("CreateViewModel", "Notification saved for user $userId")
            }
            .addOnFailureListener { e ->
                Log.e("CreateViewModel", "Error saving notification for user $userId", e)
            }
    }

    private fun loadFollowedCommunities() {
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            val userId = currentUser.uid
            viewModelScope.launch {
                try {
                    // Obtener los nombres de las comunidades seguidas desde el usuario
                    val userDoc = db.collection("users").document(userId).get().await()
                    val followedNames = userDoc.get("followedCommunities") as? List<String> ?: emptyList()

                    // Validar que se obtuvieron nombres
                    if (followedNames.isEmpty()) {
                        println("El usuario no sigue ninguna comunidad.")
                        _followedCommunities.value = emptyList()
                        return@launch
                    }

                    // Buscar documentos en la colección "communities" donde el campo "name" coincida
                    val snapshot = db.collection("communities")
                        .whereIn("name", followedNames)
                        .get()
                        .await()

                    // Mapear los documentos a objetos Community
                    val followedList = snapshot.documents.mapNotNull { document ->
                        val name = document.getString("name") ?: "Nombre desconocido"
                        val iconUrl = document.getString("iconUrl") ?: ""

                        println("Cargando comunidad: $name, Icono: $iconUrl")

                        Community(
                            id = document.id,
                            name = name,
                            description = document.getString("description") ?: "Sin descripción",
                            iconUrl = iconUrl,
                            bannerUrl = document.getString("bannerUrl"),
                            tags = document.get("tags") as? List<String> ?: emptyList(),
                            postsCount = document.getLong("postsCount")?.toInt() ?: 0,
                            createdBy = document.getString("createdBy"),
                            isOfficial = document.getBoolean("isOfficial") ?: false
                        )
                    }

                    // Actualizar el estado con las comunidades cargadas
                    _followedCommunities.value = followedList
                    println("Total comunidades cargadas: ${followedList.size}")
                } catch (e: Exception) {
                    println("Error al cargar las comunidades seguidas: ${e.message}")
                }
            }
        }
    }
}