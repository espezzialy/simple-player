package com.espezzialy.simpleplayer.presentation.album

import com.espezzialy.simpleplayer.domain.model.AlbumDetail

data class AlbumDetailUiState(
    val isLoading: Boolean = true,
    val album: AlbumDetail? = null,
    val errorMessage: String? = null,
)
