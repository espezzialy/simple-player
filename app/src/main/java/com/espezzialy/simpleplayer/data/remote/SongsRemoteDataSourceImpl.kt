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

    override suspend fun searchSongs(term: String, limit: Int, offset: Int): RemoteSearchSongsPage {
        return withContext(dispatcherProvider.io) {
            val raw = apiService
                .searchSongs(
                    term = term,
                    media = ItunesApiConstants.MEDIA_MUSIC,
                    entity = ItunesApiConstants.ENTITY_SONG,
                    limit = limit,
                    offset = offset
                )
                .results
            val filtered = raw.filter { it.kind == "song" }
            RemoteSearchSongsPage(
                dtos = filtered,
                apiConsumedCount = raw.size
            )
        }
    }

    override suspend fun lookupAlbumTracks(collectionId: Long): List<ItunesSongDto> {
        return withContext(dispatcherProvider.io) {
            apiService.lookupAlbum(
                collectionId = collectionId,
                entity = ItunesApiConstants.ENTITY_SONG,
                limit = ItunesApiConstants.LOOKUP_TRACKS_MAX
            ).results
        }
    }
}
