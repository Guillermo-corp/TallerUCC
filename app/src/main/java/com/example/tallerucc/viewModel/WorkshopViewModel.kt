package com.example.tallerucc.viewModel

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tallerucc.model.Category
import com.example.tallerucc.model.Workshop
import com.example.tallerucc.repository.WorkshopRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.time.LocalDate
import java.time.YearMonth

class WorkshopViewModel(private val repository: WorkshopRepository) : ViewModel() {
    private val _categories = MutableStateFlow<List<Category>>(emptyList())
    val categories: StateFlow<List<Category>> = _categories

    private val _workshops = MutableStateFlow<List<Workshop>>(emptyList())
    val workshops: StateFlow<List<Workshop>> = _workshops

    private val _workshopDetails = MutableStateFlow<Workshop?>(null)
    val workshopDetails: StateFlow<Workshop?> = _workshopDetails

    fun loadCategories() {
        viewModelScope.launch {
            _categories.value = repository.getCategories()
        }
    }

    fun loadWorkshopsByCategory(categoryReference: DocumentReference) {
        viewModelScope.launch {
            println("Loading workshops for categoryReference: ${categoryReference.path}") // Log
            val fetchedWorkshops = repository.getWorkshopsByCategory(categoryReference)
            println("Workshops loaded: $fetchedWorkshops") // Log
            _workshops.value = fetchedWorkshops
        }
    }


    fun loadWorkshopDetails(workshopReference: DocumentReference) {
        viewModelScope.launch {
            println("Loading workshop details for reference: ${workshopReference.path}") // Log
            val workshop = repository.getWorkshopDetails(workshopReference)
            println("Workshop details loaded: $workshop") // Log
            _workshopDetails.value = workshop
        }
    }

    fun joinCommunityByWorkshop(
        workshopName: String,
        userEmail: String,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val db = FirebaseFirestore.getInstance()

                // Buscar la comunidad por nombre
                val querySnapshot = db.collection("communities")
                    .whereEqualTo("name", workshopName)
                    .get()
                    .await()

                if (querySnapshot.documents.isNotEmpty()) {
                    val communityDoc = querySnapshot.documents[0]
                    val communityId = communityDoc.id

                    // Verificar si el usuario ya sigue la comunidad
                    val currentUser = FirebaseAuth.getInstance().currentUser
                    currentUser?.let { user ->
                        val userDoc = db.collection("users").document(user.uid).get().await()
                        val followedCommunities = userDoc.get("followedCommunities") as? List<String> ?: emptyList()
                        val registeredWorkshops = userDoc.get("registeredWorkshops") as? List<String> ?: emptyList()

                        if (followedCommunities.contains(workshopName) && registeredWorkshops.contains(workshopName)) {
                            // Si ya sigue la comunidad y está registrado en el workshop, no hacer nada
                            Log.d("WorkshopViewModel", "El usuario ya sigue la comunidad y está registrado en el workshop: $workshopName")
                            return@launch onSuccess() // Puedes manejar esto según el caso
                        }

                        // Actualizar el campo "members" de la comunidad
                        db.collection("communities").document(communityId)
                            .update("members", FieldValue.arrayUnion(userEmail))
                            .await()

                        // Incrementar el contador de miembros
                        db.collection("communities").document(communityId)
                            .update("membersCount", FieldValue.increment(1))
                            .await()

                        // Agregar la comunidad a las "followedCommunities" del usuario
                        db.collection("users").document(user.uid)
                            .update("followedCommunities", FieldValue.arrayUnion(workshopName))
                            .await()

                        if (!registeredWorkshops.contains(workshopName)) {
                            // Agregar el workshop al campo "registeredWorkshops" del usuario
                            db.collection("users").document(user.uid)
                                .update("registeredWorkshops", FieldValue.arrayUnion(workshopName))
                                .await()
                        }

                        // Registrar log
                        Log.d("WorkshopViewModel", "El usuario se unió a la comunidad: $workshopName")

                        onSuccess()
                    }
                } else {
                    throw Exception("No se encontró una comunidad con el nombre del workshop.")
                }
            } catch (e: Exception) {
                Log.e("WorkshopViewModel", "Error al unirse a la comunidad: ${e.message}")
                onFailure(e)
            }
        }
    }



}

