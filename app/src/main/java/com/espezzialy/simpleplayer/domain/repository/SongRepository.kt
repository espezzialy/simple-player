package com.espezzialy.simpleplayer.domain.repository

import com.espezzialy.simpleplayer.domain.model.AlbumDetail
import com.espezzialy.simpleplayer.domain.model.PagedSongs

interface SongRepository {
    /** Pesquisa de faixas na iTunes Search API; [PagedSongs.apiConsumedCount] reflete o tamanho bruto de `results`. */
    suspend fun searchSongs(term: String, limit: Int, offset: Int = 0): PagedSongs

    suspend fun getAlbumDetail(collectionId: Long): AlbumDetail
}
