package com.espezzialy.simpleplayer.data.repository

import com.espezzialy.simpleplayer.data.mapper.ItunesAlbumMapper
import com.espezzialy.simpleplayer.data.mapper.ItunesSongMapper
import com.espezzialy.simpleplayer.data.remote.SongsRemoteDataSource
import com.espezzialy.simpleplayer.domain.model.AlbumDetail
import com.espezzialy.simpleplayer.domain.model.PagedSongs
import com.espezzialy.simpleplayer.domain.repository.SongRepository
import javax.inject.Inject

class SongRepositoryImpl
    @Inject
    constructor(
        private val remoteDataSource: SongsRemoteDataSource,
    ) : SongRepository {
        override suspend fun searchSongs(
            term: String,
            limit: Int,
            offset: Int,
        ): PagedSongs {
            val page = remoteDataSource.searchSongs(term = term, limit = limit, offset = offset)
            return PagedSongs(
                songs = page.dtos.map(ItunesSongMapper::toSong),
                apiConsumedCount = page.apiConsumedCount,
            )
        }

        override suspend fun getAlbumDetail(collectionId: Long): AlbumDetail {
            val results = remoteDataSource.lookupAlbumTracks(collectionId = collectionId)
            return ItunesAlbumMapper.mapLookupResultsToAlbumDetail(results)
                ?: error("Could not load the album.")
        }
    }
