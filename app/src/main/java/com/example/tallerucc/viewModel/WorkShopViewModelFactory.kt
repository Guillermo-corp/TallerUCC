package com.example.tallerucc.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.tallerucc.repository.WorkshopRepository

class WorkshopViewModelFactory(
    private val repository: WorkshopRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(WorkshopViewModel::class.java)) {
            return WorkshopViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
