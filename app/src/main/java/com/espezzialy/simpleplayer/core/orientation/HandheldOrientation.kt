package com.espezzialy.simpleplayer.core.orientation

import android.app.Activity
import android.content.pm.ActivityInfo
import android.os.Build
import android.util.DisplayMetrics
import com.espezzialy.simpleplayer.ui.theme.SimplePlayerBreakpoints
import kotlin.math.min

/**
 * Shortest side of the **physical display** in dp (not the current window).
 * In tablet split-screen, [android.content.res.Configuration.smallestScreenWidthDp] can drop below 600
 * and the app would treat the player panel as phone — hence forced portrait and odd proportions.
 */
private fun Activity.physicalSmallestWidthDp(): Float {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        val bounds = windowManager.maximumWindowMetrics.bounds
        val minPx = min(bounds.width(), bounds.height()).toFloat()
        minPx / resources.displayMetrics.density
    } else {
        val metrics = DisplayMetrics()
        @Suppress("DEPRECATION")
        windowManager.defaultDisplay.getRealMetrics(metrics)
        val minPx = min(metrics.widthPixels, metrics.heightPixels).toFloat()
        minPx / metrics.density
    }
}

fun Activity.applyHandheldOrientationPolicy() {
    requestedOrientation =
        if (physicalSmallestWidthDp() < SimplePlayerBreakpoints.tabletMinWidthDp) {
            ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        } else {
            ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
        }
}
