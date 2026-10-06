package com.akhil.aimaze

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.akhil.aimaze.ui.navigation.AiMazeNavHost
import com.akhil.aimaze.ui.theme.AIMazeTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AIMazeTheme {
                AiMazeNavHost()
            }
        }
    }
}
