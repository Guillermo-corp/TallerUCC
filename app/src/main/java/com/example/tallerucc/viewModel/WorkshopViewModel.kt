package com.example.tallerucc.viewModel

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tallerucc.model.Category
import com.example.tallerucc.model.Workshop
import com.example.tallerucc.repository.WorkshopRepository
import com.google.firebase.firestore.DocumentReference
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
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


}

