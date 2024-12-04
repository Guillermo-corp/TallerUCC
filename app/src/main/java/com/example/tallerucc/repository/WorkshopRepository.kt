package com.example.tallerucc.repository

import android.content.Context
import android.net.Uri
import com.example.tallerucc.model.Category
import com.example.tallerucc.model.Schedule
import com.example.tallerucc.model.Workshop
import com.example.tallerucc.utils.Constants
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.util.Locale
import android.util.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

class WorkshopRepository {
    private val firestore = FirebaseFirestore.getInstance()

    suspend fun getCategories(): List<Category> {
        return try {
            val snapshot = firestore.collection("categories").get().await()
            snapshot.documents.map { document ->
                Category(
                    id = document.id,
                    name = document.getString("name") ?: "",
                    reference = document.reference
                )
            }
        } catch (e: Exception) {
            println("Error fetching categories: ${e.message}")
            emptyList()
        }
    }

    suspend fun getWorkshopsByCategory(categoryReference: DocumentReference): List<Workshop> {
        return try {
            println("Fetching workshops for categoryReference: ${categoryReference.path}") // Log
            val snapshot = firestore.collection("workshops")
                .whereEqualTo("categoryId", categoryReference)
                .get()
                .await()

            println("Workshops fetched: ${snapshot.documents.size}") // Log del tamaño

            snapshot.documents.map { document ->
                val imageUrls = document["imageUrls"] as? List<String>
                    ?: emptyList() // Nuevo campo para múltiples imágenes
                val firstImageUrl =
                    imageUrls.firstOrNull() ?: "" // Usa la primera imagen como vista previa

                Workshop(
                    id = document.id,
                    name = document.getString("name") ?: "",
                    description = document.getString("description") ?: "",
                    categoryId = document.getDocumentReference("categoryId"),
                    imageUrl = firstImageUrl, // Primera imagen para vistas previas
                    imageUrls = imageUrls, // Lista completa de imágenes
                    schedule = (document["schedule"] as? List<Map<String, Any>>)?.map {
                        Schedule(
                            day = it["day"] as? String ?: "",
                            startTime = (it["startTime"] as? Number)?.toLong()?.let { time ->
                                formatTimeFromLong(time)
                            } ?: "",
                            endTime = (it["endTime"] as? Number)?.toLong()?.let { time ->
                                formatTimeFromLong(time)
                            } ?: ""
                        )
                    } ?: emptyList()
                )
            }
        } catch (e: Exception) {
            println("Error fetching workshops by category: ${e.message}")
            emptyList()
        }
    }


    suspend fun getWorkshopDetails(workshopReference: DocumentReference): Workshop? {
        return try {
            val document = workshopReference.get().await()
            if (document.exists()) {
                val imageUrls = document["imageUrls"] as? List<String>
                    ?: emptyList() // Obtener lista de imágenes
                Workshop(
                    id = document.id,
                    name = document.getString("name") ?: "",
                    description = document.getString("description") ?: "",
                    categoryId = document.getDocumentReference("categoryId"),
                    imageUrl = imageUrls.firstOrNull() ?: "", // Primera imagen como vista previa
                    imageUrls = imageUrls, // Lista completa de imágenes
                    schedule = (document["schedule"] as? List<Map<String, Any>>)?.map {
                        Schedule(
                            day = it["day"] as? String ?: "",
                            startTime = (it["startTime"] as? Number)?.toLong()?.let { time ->
                                formatTimeFromLong(time)
                            } ?: "",
                            endTime = (it["endTime"] as? Number)?.toLong()?.let { time ->
                                formatTimeFromLong(time)
                            } ?: ""
                        )
                    } ?: emptyList()
                )
            } else null
        } catch (e: Exception) {
            println("Error fetching workshop details: ${e.message}")
            null
        }
    }
}

    fun formatTimeFromLong(time: Long): String {
    val hours = time / 3600000 // Extrae horas
    val minutes = (time % 3600000) / 60000 // Extrae minutos
    return String.format(Locale.getDefault(),"%02d:%02d", hours, minutes) // Formato HH:mm
}

fun uploadImageToImgur(
    imageUri: Uri,
    context: Context,
    accessToken: String,
    onComplete: (String?) -> Unit
) {
    CoroutineScope(Dispatchers.IO).launch {
        try {
            val inputStream = context.contentResolver.openInputStream(imageUri)
            val byteArray = inputStream?.readBytes()
            if (byteArray == null) {
                println("Error: No se pudo leer la imagen seleccionada.")
                withContext(Dispatchers.Main) {
                    onComplete(null)
                }
                return@launch
            }

            println("Log: Tamaño de la imagen en bytes: ${byteArray.size}")

            val base64Image = Base64.encodeToString(byteArray, Base64.NO_WRAP)
            println("Log: Tamaño del string Base64: ${base64Image.length}")

            val url = URL("https://api.imgur.com/3/image")
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "POST"
            connection.setRequestProperty("Authorization", "Bearer $accessToken")
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded")
            connection.doOutput = true

            val requestBody = "image=${Uri.encode(base64Image)}"
            println("Log: RequestBody generado para la subida")

            connection.outputStream.write(requestBody.toByteArray(Charsets.UTF_8))
            println("Log: Request enviado a la URL: $url")

            val responseCode = connection.responseCode
            val responseMessage = if (responseCode == 200) {
                connection.inputStream.bufferedReader().use { it.readText() }
            } else {
                connection.errorStream?.bufferedReader()?.use { it.readText() }
            }

            println("Log: Código de respuesta: $responseCode")
            println("Log: Respuesta del servidor: $responseMessage")

            if (responseCode == 200) {
                val jsonObject = JSONObject(responseMessage!!)
                val imageUrl = jsonObject.getJSONObject("data").getString("link")
                println("Log: Imagen subida correctamente. URL de la imagen: $imageUrl")
                withContext(Dispatchers.Main) {
                    onComplete(imageUrl)
                }
            } else {
                println("Error en la solicitud: $responseMessage")
                withContext(Dispatchers.Main) {
                    onComplete(null)
                }
            }
        } catch (e: Exception) {
            println("Error durante la subida de la imagen: ${e.message}")
            withContext(Dispatchers.Main) {
                onComplete(null)
            }
        }
    }
}





