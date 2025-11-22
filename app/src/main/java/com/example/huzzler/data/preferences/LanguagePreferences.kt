package com.example.huzzler.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.languageDataStore: DataStore<Preferences> by preferencesDataStore(name = "language_preferences")

@Singleton
class LanguagePreferences @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val LANGUAGE_CODE_KEY = stringPreferencesKey("language_code")

    /**
     * Current app language code.
     * Default: "en" (English)
     */
    val languageCode: Flow<String> = context.languageDataStore.data.map { preferences ->
        preferences[LANGUAGE_CODE_KEY] ?: "en"
    }

    /**
     * Persist selected language code (e.g., "en", "fil").
     */
    suspend fun setLanguage(languageCode: String) {
        context.languageDataStore.edit { preferences ->
            preferences[LANGUAGE_CODE_KEY] = languageCode
        }
    }
}
