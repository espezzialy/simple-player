package com.espezzialy.simpleplayer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.espezzialy.simpleplayer.di.AppContainer
import com.espezzialy.simpleplayer.presentation.songs.SongsRoute
import com.espezzialy.simpleplayer.presentation.songs.SongsViewModel
import com.espezzialy.simpleplayer.ui.theme.SimplePlayerTheme

class MainActivity : ComponentActivity() {
    private val appContainer by lazy { AppContainer() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SimplePlayerTheme {
                val songsViewModel: SongsViewModel = viewModel(
                    factory = SongsViewModel.Factory(appContainer.searchSongsUseCase)
                )
                SongsRoute(
                    viewModel = songsViewModel,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}