package com.espezzialy.simpleplayer.data.remote.api

import com.espezzialy.simpleplayer.data.remote.model.ItunesSearchResponseDto
import retrofit2.http.GET
import retrofit2.http.Query

interface ItunesApiService {
    /**
     * No default @Query values: Retrofit/Kotlin may omit the parameter from the URL, which
     * changes API behavior (e.g. missing `media` => mixed results and `kind` filtering can empty the list).
     */
    @GET("search")
    suspend fun searchSongs(
        @Query("term") term: String,
        @Query("media") media: String,
        @Query("entity") entity: String,
        @Query("limit") limit: Int,
        @Query("offset") offset: Int
    ): ItunesSearchResponseDto

    @GET("lookup")
    suspend fun lookupAlbum(
        @Query("id") collectionId: Long,
        @Query("entity") entity: String,
        @Query("limit") limit: Int
    ): ItunesSearchResponseDto
}
