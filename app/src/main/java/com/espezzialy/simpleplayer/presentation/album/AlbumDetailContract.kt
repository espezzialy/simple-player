package com.espezzialy.simpleplayer.presentation.album

import com.espezzialy.simpleplayer.domain.model.AlbumDetail

data class AlbumDetailState(
    val isLoading: Boolean = true,
    val album: AlbumDetail? = null,
    val errorMessage: String? = null
)

sealed interface AlbumDetailIntent {
    data object Retry : AlbumDetailIntent
}
