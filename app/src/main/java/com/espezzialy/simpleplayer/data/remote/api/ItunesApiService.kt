package com.espezzialy.simpleplayer.data.remote.api

import com.espezzialy.simpleplayer.data.remote.model.ItunesSearchResponseDto
import retrofit2.http.GET
import retrofit2.http.Query

interface ItunesApiService {
    /**
     * Sem valores default em parâmetros @Query: o Retrofit/Kotlin pode omitir o query na URL,
     * o que altera o comportamento da API (ex.: `media` ausente => resultados mistos e filtro
     * por `kind` pode esvaziar a lista).
     */
    @GET("search")
    suspend fun searchSongs(
        @Query("term") term: String,
        @Query("media") media: String,
        @Query("entity") entity: String,
        @Query("limit") limit: Int,
        @Query("offset") offset: Int
    ): ItunesSearchResponseDto

    /** [Lookup API](https://developer.apple.com/library/archive/documentation/AudioVideo/Conceptual/iTuneSearchAPI/Searching.html#//apple_ref/doc/uid/TP40017632-CH100-SW5) */
    @GET("lookup")
    suspend fun lookupAlbum(
        @Query("id") collectionId: Long,
        @Query("entity") entity: String,
        @Query("limit") limit: Int
    ): ItunesSearchResponseDto
}
