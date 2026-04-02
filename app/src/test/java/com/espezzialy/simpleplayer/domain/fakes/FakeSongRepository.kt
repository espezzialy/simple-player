package com.espezzialy.simpleplayer.domain.fakes

import com.espezzialy.simpleplayer.domain.model.AlbumDetail
import com.espezzialy.simpleplayer.domain.model.PagedSongs
import com.espezzialy.simpleplayer.domain.repository.SongRepository

class FakeSongRepository : SongRepository {
    var searchSongsResult: PagedSongs = PagedSongs(songs = emptyList(), apiConsumedCount = 0)
    var lastSearchTerm: String? = null
    var lastSearchLimit: Int? = null
    var lastSearchOffset: Int? = null

    var albumDetailResult: AlbumDetail =
        AlbumDetail(
            collectionId = 0L,
            title = "",
            artistName = "",
            artworkUrl = null,
            tracks = emptyList(),
        )
    var lastAlbumCollectionId: Long? = null

    override suspend fun searchSongs(
        term: String,
        limit: Int,
        offset: Int,
    ): PagedSongs {
        lastSearchTerm = term
        lastSearchLimit = limit
        lastSearchOffset = offset
        return searchSongsResult
    }

    override suspend fun getAlbumDetail(collectionId: Long): AlbumDetail {
        lastAlbumCollectionId = collectionId
        return albumDetailResult
    }
}
