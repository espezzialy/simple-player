package com.espezzialy.simpleplayer.data.repository

import com.espezzialy.simpleplayer.data.remote.SongsRemoteDataSource
import com.espezzialy.simpleplayer.domain.model.Song
import com.espezzialy.simpleplayer.domain.repository.SongRepository
import javax.inject.Inject

class SongRepositoryImpl @Inject constructor(
    private val remoteDataSource: SongsRemoteDataSource
) : SongRepository {

    override suspend fun searchSongs(term: String, limit: Int): List<Song> {
        return remoteDataSource.searchSongs(term = term, limit = limit).map { dto ->
            Song(
                trackId = dto.trackId,
                trackName = dto.trackName,
                artistName = dto.artistName,
                collectionName = dto.collectionName
            )
        }
    }
}
