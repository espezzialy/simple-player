package com.espezzialy.simpleplayer.data.remote

import com.espezzialy.simpleplayer.data.remote.model.ItunesSongDto

data class RemoteSearchSongsPage(
    val dtos: List<ItunesSongDto>,
    /** Tamanho de `results` na resposta da API antes do filtro local. */
    val apiConsumedCount: Int
)

interface SongsRemoteDataSource {
    suspend fun searchSongs(term: String, limit: Int, offset: Int): RemoteSearchSongsPage

    suspend fun lookupAlbumTracks(collectionId: Long): List<ItunesSongDto>
}
