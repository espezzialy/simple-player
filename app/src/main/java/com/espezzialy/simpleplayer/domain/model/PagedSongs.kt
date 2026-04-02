package com.espezzialy.simpleplayer.domain.model

data class PagedSongs(
    val songs: List<Song>,
    val apiConsumedCount: Int,
)
