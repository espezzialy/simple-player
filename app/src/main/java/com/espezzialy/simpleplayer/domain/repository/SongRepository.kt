package com.espezzialy.simpleplayer.domain.repository

import com.espezzialy.simpleplayer.domain.model.Song

interface SongRepository {
    suspend fun searchSongs(term: String, limit: Int): List<Song>
}
