package com.espezzialy.simpleplayer

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.espezzialy.simpleplayer.core.orientation.applyHandheldOrientationPolicy
import com.espezzialy.simpleplayer.media.PlayerNotificationConstants
import com.espezzialy.simpleplayer.presentation.navigation.SimplePlayerNavHost
import com.espezzialy.simpleplayer.ui.theme.SimplePlayerTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.MutableStateFlow

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val intentState = MutableStateFlow(Intent())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        intentState.value = intent
        applyHandheldOrientationPolicy()
        enableEdgeToEdge()
        setContent {
            val mainIntent by intentState.collectAsStateWithLifecycle()
            SimplePlayerTheme {
                SimplePlayerNavHost(
                    modifier = Modifier.fillMaxSize(),
                    mainIntent = mainIntent,
                    onConsumedOpenPlayerIntent = {
                        intent.removeExtra(PlayerNotificationConstants.EXTRA_OPEN_PLAYER)
                        intentState.value = intent
                    },
                )
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        intentState.value = intent
    }
}
