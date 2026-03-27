package com.espezzialy.simpleplayer.data.remote.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class ItunesSongDto(
    @SerializedName("trackId")
    val trackId: Long = 0L,
    @SerializedName("trackName")
    val trackName: String = "",
    @SerializedName("artistName")
    val artistName: String = "",
    @SerializedName("collectionName")
    val collectionName: String = "",
    @SerializedName("kind")
    val kind: String = ""
) : Serializable
