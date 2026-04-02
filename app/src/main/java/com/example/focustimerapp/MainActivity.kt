package com.example.focustimerapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import com.example.focustimerapp.navigation.AppNavHost
import com.example.focustimerapp.ui.theme.FocusTimerAppTheme
import com.example.focustimerapp.ui.theme.ThemeViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    /*
     Theme controller (global for the app)
     */
    private val themeViewModel: ThemeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {

            /*
             Observe theme state from DataStore (via ViewModel)
             */
            val isDarkTheme by themeViewModel.isDarkTheme.collectAsState()

            /*
             Apply theme dynamically
             */
            FocusTimerAppTheme(
                darkTheme = isDarkTheme
            ) {

                /*
                 Navigation with access to theme control
                 */
                AppNavHost(
                    themeViewModel = themeViewModel
                )
            }
        }
    }
}