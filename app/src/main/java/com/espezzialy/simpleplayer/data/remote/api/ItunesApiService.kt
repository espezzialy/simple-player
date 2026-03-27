package com.espezzialy.simpleplayer.data.remote.api

import com.espezzialy.simpleplayer.data.remote.model.ItunesSearchResponseDto
import retrofit2.http.GET
import retrofit2.http.Query

interface ItunesApiService {
    @GET("search")
    suspend fun searchSongs(
        @Query("term") term: String,
        @Query("media") media: String = "music",
        @Query("limit") limit: Int
    ): ItunesSearchResponseDto
}
