package com.example.z.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.z.utils.ThemePreferences
import kotlinx.coroutines.launch

class ThemeViewModel(private val themePreferences: ThemePreferences) : ViewModel() {
    var isDarkTheme by mutableStateOf(themePreferences.getTheme())
        private set

    fun toggleTheme(isDark: Boolean) {
        isDarkTheme = isDark
        viewModelScope.launch {
            themePreferences.saveTheme(isDark)
        }
    }
}