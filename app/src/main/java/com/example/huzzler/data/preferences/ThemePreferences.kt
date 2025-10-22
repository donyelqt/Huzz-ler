package com.example.huzzler.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Theme Preferences Manager - 2025 Best Practices
 * 
 * Uses DataStore (replaces SharedPreferences)
 * - Type-safe
 * - Async by default
 * - Built on Kotlin Coroutines and Flow
 */

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "theme_preferences")

@Singleton
class ThemePreferences @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val DARK_MODE_KEY = booleanPreferencesKey("dark_mode")
    
    /**
     * Get dark mode state as Flow
     * Default: false (light mode)
     */
    val isDarkMode: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[DARK_MODE_KEY] ?: false
    }
    
    /**
     * Set dark mode state
     */
    suspend fun setDarkMode(isDarkMode: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[DARK_MODE_KEY] = isDarkMode
        }
    }
    
    /**
     * Toggle dark mode
     */
    suspend fun toggleDarkMode() {
        context.dataStore.edit { preferences ->
            val currentMode = preferences[DARK_MODE_KEY] ?: false
            preferences[DARK_MODE_KEY] = !currentMode
        }
    }
}
