package com.espezzialy.simpleplayer.presentation.player

import android.net.Uri
import androidx.navigation.NavController
import com.espezzialy.simpleplayer.domain.model.Song

object PlayerNavigation {
    const val ROUTE = "player/{trackId}/{trackName}/{artistName}/{artworkUrl}/{collectionId}"

    const val ARG_TRACK_ID = "trackId"
    const val ARG_TRACK_NAME = "trackName"
    const val ARG_ARTIST_NAME = "artistName"
    const val ARG_ARTWORK_URL = "artworkUrl"
    const val ARG_COLLECTION_ID = "collectionId"

    /** Valor sentinela na rota quando a faixa não tem álbum na API. */
    const val NO_COLLECTION_ID = -1L
}

fun NavController.navigateToPlayer(song: Song) {
    val collectionSegment = song.collectionId ?: PlayerNavigation.NO_COLLECTION_ID
    navigate(
        "player/${song.trackId}/" +
            "${Uri.encode(song.trackName)}/" +
            "${Uri.encode(song.artistName)}/" +
            "${Uri.encode(song.artworkUrl100 ?: "")}/" +
            collectionSegment
    )
}
