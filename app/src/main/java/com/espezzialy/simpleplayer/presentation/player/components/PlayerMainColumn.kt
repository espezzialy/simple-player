package com.espezzialy.simpleplayer.presentation.player.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import com.espezzialy.simpleplayer.presentation.player.PlayerIntent
import com.espezzialy.simpleplayer.presentation.player.PlayerUiState
import com.espezzialy.simpleplayer.ui.theme.PlayerNowPlayingTextStyles
import com.espezzialy.simpleplayer.ui.theme.SimplePlayerDimens

@Composable
fun PlayerMainColumn(
    state: PlayerUiState,
    onIntent: (PlayerIntent) -> Unit,
    artistNameColor: Color,
    artworkSize: Dp,
    contentPaddingTop: Dp,
    seekTrackHeight: Dp,
    seekThumbDiameter: Dp,
    isTabletLayout: Boolean,
) {
    val colorScheme = MaterialTheme.colorScheme
    val trackNameStyle =
        if (isTabletLayout) {
            PlayerNowPlayingTextStyles.trackTablet
        } else {
            PlayerNowPlayingTextStyles.trackPhone
        }
    val artistNameStyle =
        if (isTabletLayout) {
            PlayerNowPlayingTextStyles.artistTablet
        } else {
            PlayerNowPlayingTextStyles.artistPhone
        }

    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .padding(top = contentPaddingTop),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        if (isTabletLayout) {
            Box(
                modifier =
                    Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                contentAlignment = Alignment.Center,
            ) {
                PlayerArtwork(
                    artworkUrl = state.artworkUrl,
                    trackName = state.trackName,
                    size = artworkSize,
                    onSwipeToNext = { onIntent(PlayerIntent.SkipNextClicked) },
                    onSwipeToPrevious = { onIntent(PlayerIntent.SkipPreviousClicked) },
                )
            }
        } else {
            PlayerArtwork(
                artworkUrl = state.artworkUrl,
                trackName = state.trackName,
                size = artworkSize,
                onSwipeToNext = { onIntent(PlayerIntent.SkipNextClicked) },
                onSwipeToPrevious = { onIntent(PlayerIntent.SkipPreviousClicked) },
            )
            Spacer(modifier = Modifier.weight(1f))
        }
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = state.trackName,
                style = trackNameStyle,
                color = colorScheme.onBackground,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Start,
            )
            Spacer(modifier = Modifier.height(SimplePlayerDimens.Player.spacerTitleToArtist))
            Text(
                text = state.artistName,
                style = artistNameStyle,
                color = artistNameColor,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Start,
            )
            Spacer(modifier = Modifier.height(SimplePlayerDimens.Player.spacerArtistToSeek))
            PlayerSeekSection(
                progress = state.progress,
                currentLabel = state.currentTimeLabel,
                remainingLabel = state.remainingTimeLabel,
                onProgressChange = { onIntent(PlayerIntent.ProgressChanged(it)) },
                trackHeight = seekTrackHeight,
                thumbDiameter = seekThumbDiameter,
            )
        }
        Spacer(
            modifier =
                Modifier.height(
                    if (isTabletLayout) {
                        SimplePlayerDimens.Player.spacerSeekToTransportTablet
                    } else {
                        SimplePlayerDimens.Player.spacerSeekToTransportPhone
                    },
                ),
        )
        PlayerTransportControls(
            isPlaying = state.isPlaying,
            repeatEnabled = state.repeatEnabled,
            onPlayPause = { onIntent(PlayerIntent.PlayPauseClicked) },
            onPrevious = { onIntent(PlayerIntent.SkipPreviousClicked) },
            onNext = { onIntent(PlayerIntent.SkipNextClicked) },
            onRepeat = { onIntent(PlayerIntent.RepeatClicked) },
        )
        Spacer(modifier = Modifier.height(SimplePlayerDimens.Player.spacerAfterTransport))
    }
}
