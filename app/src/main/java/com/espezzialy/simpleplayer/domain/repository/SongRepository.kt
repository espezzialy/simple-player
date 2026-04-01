package com.espezzialy.simpleplayer.domain.repository

import com.espezzialy.simpleplayer.domain.model.AlbumDetail
import com.espezzialy.simpleplayer.domain.model.PagedSongs

interface SongRepository {
    suspend fun searchSongs(term: String, limit: Int, offset: Int = 0): PagedSongs

    suspend fun getAlbumDetail(collectionId: Long): AlbumDetail
}
