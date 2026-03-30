package com.espezzialy.simpleplayer.core.media

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class ItunesArtworkUrlTest {

    @Test
    fun toItunesArtwork600_replaces100suffix() {
        val input =
            "https://is1-ssl.mzstatic.com/image/thumb/foo/0.jpg/100x100bb.jpg"
        assertEquals(
            "https://is1-ssl.mzstatic.com/image/thumb/foo/0.jpg/600x600bb.jpg",
            input.toItunesArtwork600()
        )
    }

    @Test
    fun toItunesArtwork600_idempotent_whenAlready600() {
        val url = "https://example.com/path/0.jpg/600x600bb.jpg"
        assertEquals(url, url.toItunesArtwork600())
    }

    @Test
    fun toItunesArtwork600_returnsNull_whenBlank() {
        assertNull(null.toItunesArtwork600())
        assertNull("".toItunesArtwork600())
        assertNull("   ".toItunesArtwork600())
    }

    @Test
    fun toItunesArtwork200_replaces100suffix() {
        val input =
            "https://is1-ssl.mzstatic.com/image/thumb/foo/0.jpg/100x100bb.jpg"
        assertEquals(
            "https://is1-ssl.mzstatic.com/image/thumb/foo/0.jpg/200x200bb.jpg",
            input.toItunesArtwork200()
        )
    }

    @Test
    fun toItunesArtwork200_idempotent_whenAlready200() {
        val url = "https://example.com/path/0.jpg/200x200bb.jpg"
        assertEquals(url, url.toItunesArtwork200())
    }
}
