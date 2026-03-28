package com.espezzialy.simpleplayer.data.repository

import com.espezzialy.simpleplayer.data.mapper.ItunesAlbumMapper
import com.espezzialy.simpleplayer.data.remote.SongsRemoteDataSource
import com.espezzialy.simpleplayer.domain.model.AlbumDetail
import com.espezzialy.simpleplayer.domain.model.Song
import com.espezzialy.simpleplayer.domain.repository.SongRepository
import javax.inject.Inject

class SongRepositoryImpl @Inject constructor(
    private val remoteDataSource: SongsRemoteDataSource
) : SongRepository {

    override suspend fun searchSongs(term: String, limit: Int): List<Song> {
        return remoteDataSource.searchSongs(term = term, limit = limit).map { dto ->
            Song(
                trackId = dto.trackId ?: 0L,
                trackName = dto.trackName.orEmpty(),
                artistName = dto.artistName.orEmpty(),
                collectionName = dto.collectionName.orEmpty(),
                collectionId = dto.collectionId,
                artworkUrl100 = dto.artworkUrl100?.takeIf { it.isNotBlank() }
            )
        }
    }

    override suspend fun getAlbumDetail(collectionId: Long): AlbumDetail {
        val results = remoteDataSource.lookupAlbumTracks(collectionId = collectionId)
        return ItunesAlbumMapper.mapLookupResultsToAlbumDetail(results)
            ?: error("Could not load the album.")
    }
}
