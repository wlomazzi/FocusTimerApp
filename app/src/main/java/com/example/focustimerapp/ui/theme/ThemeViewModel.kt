package com.example.focustimerapp.ui.theme

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.focustimerapp.core.datastore.getTheme
import com.example.focustimerapp.core.datastore.saveTheme
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ThemeViewModel(application: Application) : AndroidViewModel(application) {

    /*
    * Reactive theme state from DataStore
    */
    val isDarkTheme = getTheme(application.applicationContext)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = false
        )

    /*
    * Toggle and persist theme
    */
    fun toggleTheme() {
        viewModelScope.launch {
            val current = isDarkTheme.value
            saveTheme(getApplication<Application>().applicationContext, !current)
        }
    }

    fun setDarkTheme(enabled: Boolean) {
        viewModelScope.launch {
            saveTheme(getApplication<Application>().applicationContext, enabled)
        }
    }
}