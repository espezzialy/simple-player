package com.espezzialy.simpleplayer.data.mapper

import com.espezzialy.simpleplayer.data.remote.model.ItunesSongDto
import com.espezzialy.simpleplayer.domain.model.AlbumDetail
import com.espezzialy.simpleplayer.domain.model.AlbumTrack

object ItunesAlbumMapper {
    fun mapLookupResultsToAlbumDetail(results: List<ItunesSongDto>): AlbumDetail? {
        if (results.isEmpty()) return null

        val tracksRaw = results.filter { it.kind == "song" && (it.trackId ?: 0L) > 0L }
        val collection = results.firstOrNull { it.wrapperType == "collection" }

        val collectionId =
            collection?.collectionId
                ?: tracksRaw.firstNotNullOfOrNull { it.collectionId }
                ?: return null

        val title =
            collection?.collectionName?.takeIf { it.isNotBlank() }
                ?: tracksRaw.firstOrNull()?.collectionName.orEmpty()
        val artist =
            collection?.artistName?.takeIf { it.isNotBlank() }
                ?: tracksRaw.firstOrNull()?.artistName.orEmpty()

        val artwork =
            listOfNotNull(
                collection?.artworkUrl600,
                collection?.artworkUrl100,
                tracksRaw.firstOrNull()?.artworkUrl600,
                tracksRaw.firstOrNull()?.artworkUrl100,
            ).firstOrNull { !it.isNullOrBlank() }

        val tracks =
            tracksRaw
                .sortedWith(
                    compareBy<ItunesSongDto>({ it.discNumber ?: 0 }, { it.trackNumber ?: 0 }),
                )
                .map { dto ->
                    AlbumTrack(
                        trackId = dto.trackId ?: 0L,
                        trackName = dto.trackName.orEmpty(),
                        artistName = dto.artistName.orEmpty(),
                        artworkUrl100 = dto.artworkUrl100?.takeIf { it.isNotBlank() },
                    )
                }

        return AlbumDetail(
            collectionId = collectionId,
            title = title,
            artistName = artist,
            artworkUrl = artwork,
            tracks = tracks,
        )
    }
}
