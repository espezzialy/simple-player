package com.espezzialy.simpleplayer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import com.espezzialy.simpleplayer.core.orientation.applyHandheldOrientationPolicy
import com.espezzialy.simpleplayer.presentation.navigation.SimplePlayerNavHost
import com.espezzialy.simpleplayer.ui.theme.SimplePlayerTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        applyHandheldOrientationPolicy()
        enableEdgeToEdge()
        setContent {
            SimplePlayerTheme {
                SimplePlayerNavHost(modifier = Modifier.fillMaxSize())
            }
        }
    }
}
