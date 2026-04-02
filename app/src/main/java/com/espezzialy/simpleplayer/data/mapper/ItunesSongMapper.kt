package com.espezzialy.simpleplayer.data.mapper

import com.espezzialy.simpleplayer.data.remote.model.ItunesSongDto
import com.espezzialy.simpleplayer.domain.model.Song

object ItunesSongMapper {
    fun toSong(dto: ItunesSongDto): Song =
        Song(
            trackId = dto.trackId ?: 0L,
            trackName = dto.trackName.orEmpty(),
            artistName = dto.artistName.orEmpty(),
            collectionName = dto.collectionName.orEmpty(),
            collectionId = dto.collectionId,
            artworkUrl100 = dto.artworkUrl100?.takeIf { it.isNotBlank() },
            trackTimeMillis = dto.trackTimeMillis?.takeIf { it > 0L },
        )
}
