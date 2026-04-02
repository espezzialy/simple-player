package com.espezzialy.simpleplayer.di

import com.espezzialy.simpleplayer.data.local.RecentSongSnapshot
import com.espezzialy.simpleplayer.data.local.RecentSongsJsonCodec
import org.junit.Assert.assertEquals
import org.junit.Test

/** Ensures [GsonModule.provideGson] works with [RecentSongsJsonCodec] (production wiring). */
class GsonModuleTest {
    @Test
    fun provideGson_roundTripsRecentSongSnapshots() {
        val fromModule = GsonModule.provideGson()
        val snapshot =
            RecentSongSnapshot(
                trackId = 1L,
                trackName = "T",
                artistName = "A",
                collectionName = "C",
                collectionId = 2L,
                artworkUrlSmall = "s",
                artworkUrlLarge = "l",
            )
        val codec = RecentSongsJsonCodec(fromModule)
        val roundTrip = codec.decode(codec.encode(listOf(snapshot)))
        assertEquals(1, roundTrip.size)
        assertEquals(snapshot, roundTrip[0])
    }
}
