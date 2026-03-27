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
        @Query("limit") limit: Int
    ): ItunesSearchResponseDto
}
