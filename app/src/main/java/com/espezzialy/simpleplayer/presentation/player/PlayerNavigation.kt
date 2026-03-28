package com.espezzialy.simpleplayer.presentation.player

import android.net.Uri
import androidx.navigation.NavController
import com.espezzialy.simpleplayer.domain.model.Song

object PlayerNavigation {
    const val ROUTE = "player/{trackId}/{trackName}/{artistName}/{artworkUrl}"

    const val ARG_TRACK_ID = "trackId"
    const val ARG_TRACK_NAME = "trackName"
    const val ARG_ARTIST_NAME = "artistName"
    const val ARG_ARTWORK_URL = "artworkUrl"
}

fun NavController.navigateToPlayer(song: Song) {
    navigate(
        "player/${song.trackId}/" +
            "${Uri.encode(song.trackName)}/" +
            "${Uri.encode(song.artistName)}/" +
            Uri.encode(song.artworkUrl100 ?: "")
    )
}
