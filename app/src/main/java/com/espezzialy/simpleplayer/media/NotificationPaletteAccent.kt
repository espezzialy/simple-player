package com.espezzialy.simpleplayer.media

import android.graphics.Bitmap
import androidx.palette.graphics.Palette

internal fun computeAccentArgbFromBitmap(
    bitmap: Bitmap,
    fallbackArgb: Int,
): Int {
    val palette = Palette.from(bitmap).generate()
    return palette.darkMutedSwatch?.rgb
        ?: palette.vibrantSwatch?.rgb
        ?: palette.dominantSwatch?.rgb
        ?: fallbackArgb
}
