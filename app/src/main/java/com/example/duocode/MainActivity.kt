package com.example.duocode

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.duocode.navigation.AppNavigation
import com.example.duocode.ui.theme.DuoCodeTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DuoCodeTheme(dynamicColor = false) {
                AppNavigation()
            }
        }
    }
}