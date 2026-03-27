package com.espezzialy.simpleplayer.data.remote.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class ItunesSearchResponseDto(
    @SerializedName("resultCount")
    val resultCount: Int = 0,
    @SerializedName("results")
    val results: List<ItunesSongDto> = emptyList()
) : Serializable
