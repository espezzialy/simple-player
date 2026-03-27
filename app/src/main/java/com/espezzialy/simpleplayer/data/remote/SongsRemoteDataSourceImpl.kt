package com.espezzialy.simpleplayer.data.remote

import com.espezzialy.simpleplayer.core.coroutines.DispatcherProvider
import com.espezzialy.simpleplayer.data.remote.api.ItunesApiService
import com.espezzialy.simpleplayer.data.remote.model.ItunesSongDto
import kotlinx.coroutines.withContext
import javax.inject.Inject

class SongsRemoteDataSourceImpl @Inject constructor(
    private val apiService: ItunesApiService,
    private val dispatcherProvider: DispatcherProvider
) : SongsRemoteDataSource {

    override suspend fun searchSongs(term: String, limit: Int): List<ItunesSongDto> {
        return withContext(dispatcherProvider.io) {
            apiService
                .searchSongs(
                    term = term,
                    media = MEDIA_MUSIC,
                    entity = ENTITY_SONG,
                    limit = limit
                )
                .results
                .filter { it.kind == "song" }
        }
    }

    override suspend fun lookupAlbumTracks(collectionId: Long): List<ItunesSongDto> {
        return withContext(dispatcherProvider.io) {
            apiService.lookupAlbum(
                collectionId = collectionId,
                entity = ENTITY_SONG,
                limit = LOOKUP_LIMIT
            ).results
        }
    }

    private companion object {
        const val MEDIA_MUSIC = "music"
        const val ENTITY_SONG = "song"
        const val LOOKUP_LIMIT = 200
    }
}
