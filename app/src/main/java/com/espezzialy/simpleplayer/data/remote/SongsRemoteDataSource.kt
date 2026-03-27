package com.espezzialy.simpleplayer.data.remote

import com.espezzialy.simpleplayer.data.remote.model.ItunesSongDto

interface SongsRemoteDataSource {
    suspend fun searchSongs(term: String, limit: Int): List<ItunesSongDto>

    suspend fun lookupAlbumTracks(collectionId: Long): List<ItunesSongDto>
}
