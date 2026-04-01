package com.espezzialy.simpleplayer.core.orientation

import android.app.Activity
import android.content.pm.ActivityInfo
import android.os.Build
import android.util.DisplayMetrics
import kotlin.math.min

/** Mesmo breakpoint que o layout tablet (ex. player / listas). */
private const val TABLET_SMALLEST_WIDTH_DP = 600

/**
 * Menor lado do **ecrã físico** em dp (não o da janela atual).
 * Em split-screen no tablet, [android.content.res.Configuration.smallestScreenWidthDp] cai abaixo de 600
 * e fazia o app tratar o painel como telefone — daí o retrato forçado e proporções estranhas.
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

/**
 * Telefone (ecrã físico estreito): força retrato. Tablet: rotação livre, inclusive em split-screen.
 */
fun Activity.applyHandheldOrientationPolicy() {
    requestedOrientation =
        if (physicalSmallestWidthDp() < TABLET_SMALLEST_WIDTH_DP) {
            ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        } else {
            ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
        }
}
