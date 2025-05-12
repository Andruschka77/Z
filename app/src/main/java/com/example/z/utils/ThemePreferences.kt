package com.example.z.utils

import android.content.Context
import androidx.core.content.edit

class ThemePreferences(context: Context) {
    private val sharedPreferences = context.getSharedPreferences("theme_prefs", Context.MODE_PRIVATE)

    fun saveTheme(isDark: Boolean) {
        sharedPreferences.edit {
            putBoolean("is_dark_theme", isDark)
        }
    }

    fun getTheme(): Boolean {
        return sharedPreferences.getBoolean("is_dark_theme", false)
    }
}