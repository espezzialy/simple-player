package com.espezzialy.simpleplayer.data.remote

import com.espezzialy.simpleplayer.core.coroutines.DispatcherProvider
import com.espezzialy.simpleplayer.data.remote.api.ItunesApiService
import com.espezzialy.simpleplayer.data.remote.model.ItunesSongDto
import kotlinx.coroutines.withContext

class SongsRemoteDataSourceImpl(
    private val apiService: ItunesApiService,
    private val dispatcherProvider: DispatcherProvider
) : SongsRemoteDataSource {

    override suspend fun searchSongs(term: String, limit: Int): List<ItunesSongDto> {
        return withContext(dispatcherProvider.io) {
            apiService
                .searchSongs(term = term, limit = limit)
                .results
                .filter { it.kind == "song" }
        }
    }
}
