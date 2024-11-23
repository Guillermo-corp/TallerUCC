package com.example.tallerucc.repository

import com.example.tallerucc.model.Category
import com.example.tallerucc.model.Schedule
import com.example.tallerucc.model.Workshop
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class WorkshopRepository {
    private val firestore = FirebaseFirestore.getInstance()

    suspend fun getCategories(): List<Category> {
        return try {
            val snapshot = firestore.collection("categories").get().await()
            snapshot.documents.map { document ->
                Category(
                    id = document.id,
                    name = document.getString("name") ?: ""
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
                Workshop(
                    id = document.id,
                    name = document.getString("name") ?: "",
                    description = document.getString("description") ?: "",
                    categoryId = document.getDocumentReference("categoryId"),
                    imageUrl = document.getString("imageUrl") ?: "",
                    schedule = (document["schedule"] as? List<Map<String, String>>)?.map {
                        Schedule(
                            day = it["day"] ?: "",
                            startTime = it["startTime"] ?: "",
                            endTime = it["endTime"] ?: ""
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
                Workshop(
                    id = document.id,
                    name = document.getString("name") ?: "",
                    description = document.getString("description") ?: "",
                    categoryId = document.getDocumentReference("categoryId"), // Usamos DocumentReference
                    imageUrl = document.getString("imageUrl") ?: "",
                    schedule = (document["schedule"] as? List<Map<String, String>>)?.map {
                        Schedule(
                            day = it["day"] ?: "",
                            startTime = it["startTime"] ?: "",
                            endTime = it["endTime"] ?: ""
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

