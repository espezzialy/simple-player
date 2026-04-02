package com.espezzialy.simpleplayer.data.fakes

import com.espezzialy.simpleplayer.data.remote.RemoteSearchSongsPage
import com.espezzialy.simpleplayer.data.remote.SongsRemoteDataSource
import com.espezzialy.simpleplayer.data.remote.model.ItunesSongDto

class FakeSongsRemoteDataSource : SongsRemoteDataSource {
    var searchPage: RemoteSearchSongsPage = RemoteSearchSongsPage(dtos = emptyList(), apiConsumedCount = 0)
    var lastSearchTerm: String? = null
    var lastSearchLimit: Int? = null
    var lastSearchOffset: Int? = null
    var lookupResults: List<ItunesSongDto> = emptyList()
    var lastLookupCollectionId: Long? = null

    override suspend fun searchSongs(
        term: String,
        limit: Int,
        offset: Int,
    ): RemoteSearchSongsPage {
        lastSearchTerm = term
        lastSearchLimit = limit
        lastSearchOffset = offset
        return searchPage
    }

    override suspend fun lookupAlbumTracks(collectionId: Long): List<ItunesSongDto> {
        lastLookupCollectionId = collectionId
        return lookupResults
    }
}
