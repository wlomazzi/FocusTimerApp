package com.example.focustimerapp.core.datastore

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// This creates a single instance of DataStore
val Context.dataStore by preferencesDataStore(name = "settings")

val DARK_THEME_KEY = booleanPreferencesKey("dark_theme")


suspend fun saveTheme(context: Context, isDark: Boolean) {
    context.dataStore.edit { preferences ->
        preferences[DARK_THEME_KEY] = isDark
    }
}

fun getTheme(context: Context): Flow<Boolean> {
    return context.dataStore.data
        .map { preferences ->
            preferences[DARK_THEME_KEY] ?: false
        }
}