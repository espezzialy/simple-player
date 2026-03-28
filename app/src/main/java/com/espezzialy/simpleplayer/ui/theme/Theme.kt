package com.espezzialy.simpleplayer.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

/**
 * Dark palette used across the app (search, player, album). The UI is dark-first; tokens live in
 * [SimplePlayerColors] and map into Material [androidx.compose.material3.ColorScheme] so screens use
 * [MaterialTheme.colorScheme] instead of ad-hoc color objects.
 */
private val SimplePlayerDarkColorScheme = darkColorScheme(
    primary = SimplePlayerColors.SeekTrackActive,
    onPrimary = SimplePlayerColors.Background,
    secondary = SimplePlayerColors.Surface,
    onSecondary = SimplePlayerColors.OnBackground,
    tertiary = SimplePlayerColors.ControlSurface,
    onTertiary = SimplePlayerColors.OnBackground,
    background = SimplePlayerColors.Background,
    onBackground = SimplePlayerColors.OnBackground,
    surface = SimplePlayerColors.Surface,
    onSurface = SimplePlayerColors.OnBackground,
    surfaceVariant = SimplePlayerColors.SeekTrackInactive,
    onSurfaceVariant = SimplePlayerColors.OnBackgroundMuted,
    surfaceContainerHigh = SimplePlayerColors.ControlSurface,
    surfaceContainerLowest = SimplePlayerColors.ArtworkPlaceholder,
    outline = SimplePlayerColors.OnBackgroundMuted
)

@Composable
fun SimplePlayerTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = SimplePlayerDarkColorScheme,
        typography = Typography,
        content = content
    )
}
