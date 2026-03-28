package com.espezzialy.simpleplayer.ui.theme

import androidx.compose.ui.graphics.Color

/**
 * Semantic color tokens for the dark player UI (aligned with the Figma dark screens).
 * Wired into [SimplePlayerDarkColorScheme] in [Theme.kt] and exposed via [androidx.compose.material3.MaterialTheme.colorScheme].
 */
object SimplePlayerColors {
    val Background = Color(0xFF000000)
    val OnBackground = Color(0xFFFFFFFF)
    val OnBackgroundMuted = Color(0xFFB3B3B3)
    val Surface = Color(0xFF1C1C1E)
    val ControlSurface = Color(0xFF2C2C2C)
    val ArtworkPlaceholder = Color(0xFF1A1A1A)
    val SeekTrackInactive = Color(0xFF3A3A3C)
    val SeekTrackActive = Color(0xFFFFFFFF)
}
