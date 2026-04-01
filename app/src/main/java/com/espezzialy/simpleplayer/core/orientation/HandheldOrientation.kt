package com.espezzialy.simpleplayer.core.orientation

import android.app.Activity
import android.content.pm.ActivityInfo

/** Mesmo breakpoint que o layout tablet (ex. [com.espezzialy.simpleplayer.presentation.player.PlayerScreen]). */
private const val TABLET_SMALLEST_WIDTH_DP = 600

/**
 * Telefone: força retrato. Tablet: permite rotação livre.
 */
fun Activity.applyHandheldOrientationPolicy() {
    requestedOrientation =
        if (resources.configuration.smallestScreenWidthDp < TABLET_SMALLEST_WIDTH_DP) {
            ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        } else {
            ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
        }
}
