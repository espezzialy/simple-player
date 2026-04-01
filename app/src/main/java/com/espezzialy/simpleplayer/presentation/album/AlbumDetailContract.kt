package com.espezzialy.simpleplayer.presentation.album

sealed interface AlbumDetailIntent {
    data object Retry : AlbumDetailIntent
}
