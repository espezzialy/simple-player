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

    /** Igual ao startDestination do [com.espezzialy.simpleplayer.presentation.navigation.SimplePlayerNavHost]. */
    const val SONGS_START_ROUTE = "songs"
}

fun playerRouteFor(song: Song): String {
    val collectionSegment = song.collectionId ?: PlayerNavigation.NO_COLLECTION_ID
    return "player/${song.trackId}/" +
        "${Uri.encode(song.trackName)}/" +
        "${Uri.encode(song.artistName)}/" +
        "${Uri.encode(song.artworkUrl100 ?: "")}/" +
        collectionSegment
}

/** Empilha por cima da lista (Songs → Player). */
fun NavController.navigateToPlayer(song: Song) {
    navigate(playerRouteFor(song))
}

/**
 * Abre o player a partir do álbum: volta até a lista e empilha um único Player,
 * evitando Songs → Player → Album → Player → …
 */
fun NavController.navigateToPlayerFromAlbum(song: Song) {
    navigate(playerRouteFor(song)) {
        popUpTo(PlayerNavigation.SONGS_START_ROUTE) { inclusive = false }
    }
}
