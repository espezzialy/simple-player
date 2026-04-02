package com.espezzialy.simpleplayer

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.painterResource
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.espezzialy.simpleplayer.core.orientation.applyHandheldOrientationPolicy
import com.espezzialy.simpleplayer.ui.theme.SimplePlayerColors
import com.espezzialy.simpleplayer.ui.theme.SimplePlayerDimens
import kotlinx.coroutines.delay

class SplashActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        applyHandheldOrientationPolicy()
        enableEdgeToEdge()
        setContent {
            SplashContent()
        }
    }

    private fun openMain() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    @Composable
    private fun SplashContent() {
        val gradient =
            Brush.linearGradient(
                colors = listOf(SimplePlayerColors.Background, SimplePlayerColors.SplashGradientEnd),
                start = Offset(0f, 1000f),
                end = Offset(1000f, 0f),
            )

        Box(
            modifier =
                Modifier
                    .fillMaxSize()
                    .background(gradient),
            contentAlignment = Alignment.Center,
        ) {
            Image(
                painter = painterResource(id = R.drawable.music_note_image),
                contentDescription = null,
                modifier = Modifier.size(SimplePlayerDimens.Splash.logoSize),
            )
        }

        LaunchedEffect(Unit) {
            delay(750)
            openMain()
        }
    }
}
