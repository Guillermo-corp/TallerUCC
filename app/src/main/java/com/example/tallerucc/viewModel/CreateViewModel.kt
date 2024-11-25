package com.example.tallerucc.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tallerucc.model.Category
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.tasks.await

import android.content.Context
import androidx.fragment.app.FragmentManager
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat

class CreateViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()

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






    fun createPost(name: String, description: String, content: String) {
        val post = mapOf(
            "title" to name,
            "textContent" to content,
            "description" to description,
            "createdAt" to Timestamp.now(),
            "type" to "post"
        )
        db.collection("posts").add(post)
            .addOnSuccessListener { Log.d("CreatePage", "Post created successfully!") }
            .addOnFailureListener { e -> Log.e("CreatePage", "Error creating post", e) }
    }

    fun createCommunity(name: String, description: String) {
        val community = mapOf(
            "name" to name,
            "description" to description,
            "createdAt" to Timestamp.now(),
            "members" to listOf<String>()
        )
        db.collection("communities").add(community)
            .addOnSuccessListener { Log.d("CreatePage", "Community created successfully!") }
            .addOnFailureListener { e -> Log.e("CreatePage", "Error creating community", e) }
    }
}
