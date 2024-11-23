package com.example.tallerucc.viewModel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class NavigationViewModel : ViewModel() {
    private val _selectedIndex = MutableStateFlow(0)
    val selectedIndex: StateFlow<Int> = _selectedIndex

    fun selectIndex(index: Int) {
        _selectedIndex.value = index
    }

    fun updateSelectedIndexBasedOnRoute(route: String?) {
        val navItems = listOf(
            "home",
            "communities",
            "workshops",
            "notifications"
        )
        val index = navItems.indexOf(route)
        if (index >= 0) {
            selectIndex(index)
        }
    }
}



