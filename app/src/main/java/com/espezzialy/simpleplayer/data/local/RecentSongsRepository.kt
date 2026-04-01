package com.espezzialy.simpleplayer.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import coil.imageLoader
import coil.request.ImageRequest
import com.espezzialy.simpleplayer.core.coroutines.DispatcherProvider
import com.espezzialy.simpleplayer.domain.model.Song
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext

private val Context.recentSongsDataStore: DataStore<Preferences> by preferencesDataStore(
    name = "recent_songs"
)

@Singleton
class RecentSongsRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    private val gson: Gson,
    private val dispatcherProvider: DispatcherProvider
) {

    private val scope = CoroutineScope(SupervisorJob() + dispatcherProvider.io)
    private val jsonKey = stringPreferencesKey("recent_songs_json")
    private val writeMutex = Mutex()

    val recentSongs: StateFlow<List<Song>> = context.recentSongsDataStore.data
        .map { prefs ->
            try {
                val json = prefs[jsonKey]
                parseSnapshots(json).map { it.toSong() }
            } catch (_: Exception) {
                emptyList()
            }
        }
        .stateIn(scope, SharingStarted.WhileSubscribed(5_000), emptyList())

    suspend fun add(song: Song): List<Song> {
        val snap = RecentSongSnapshot.fromSong(song)
        val newSnapshots = writeMutex.withLock {
            withContext(dispatcherProvider.io) {
                val prefs = context.recentSongsDataStore.data.first()
                val current = parseSnapshots(prefs[jsonKey])
                val filtered = current.filter { it.trackId != song.trackId }
                val list = listOf(snap) + filtered.take(MAX_RECENT - 1)
                context.recentSongsDataStore.edit { e ->
                    e[jsonKey] = gson.toJson(list)
                }
                list
            }
        }
        prefetchArtworkForOffline(snap)
        return newSnapshots.map { it.toSong() }
    }

    suspend fun clear() {
        writeMutex.withLock {
            withContext(dispatcherProvider.io) {
                context.recentSongsDataStore.edit { it.remove(jsonKey) }
            }
        }
    }

    private fun prefetchArtworkForOffline(snap: RecentSongSnapshot) {
        scope.launch(dispatcherProvider.io) {
            val loader = context.imageLoader
            val urls = listOfNotNull(snap.artworkUrlSmall, snap.artworkUrlLarge).distinct()
            for (url in urls) {
                loader.enqueue(ImageRequest.Builder(context).data(url).build())
            }
        }
    }

    private fun parseSnapshots(json: String?): List<RecentSongSnapshot> {
        if (json.isNullOrBlank()) return emptyList()
        return try {
            val type = object : TypeToken<List<RecentSongSnapshot>>() {}.type
            gson.fromJson<List<RecentSongSnapshot>>(json, type) ?: emptyList()
        } catch (_: Exception) {
            emptyList()
        }
    }

    private companion object {
        const val MAX_RECENT = 50
    }
}
