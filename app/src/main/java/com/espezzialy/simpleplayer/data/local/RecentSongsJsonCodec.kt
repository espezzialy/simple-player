package com.espezzialy.simpleplayer.data.local

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RecentSongsJsonCodec @Inject constructor(
    private val gson: Gson
) {

    fun decode(json: String?): List<RecentSongSnapshot> {
        if (json.isNullOrBlank()) return emptyList()
        return try {
            val type = object : TypeToken<List<RecentSongSnapshot>>() {}.type
            gson.fromJson<List<RecentSongSnapshot>>(json, type) ?: emptyList()
        } catch (_: Exception) {
            emptyList()
        }
    }

    fun encode(snapshots: List<RecentSongSnapshot>): String = gson.toJson(snapshots)
}
