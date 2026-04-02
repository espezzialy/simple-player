package com.espezzialy.simpleplayer.media

import android.graphics.Bitmap
import android.graphics.Color
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28])
class NotificationPaletteAccentTest {
    @Test
    fun computeAccentArgbFromBitmap_returnsOpaqueArgbFromPalette() {
        val bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)
        bitmap.setPixel(0, 0, Color.RED)
        val fallback = 0xFF111111.toInt()
        val accent = computeAccentArgbFromBitmap(bitmap, fallback)
        assertEquals(0xFF, accent ushr 24 and 0xFF)
    }
}
