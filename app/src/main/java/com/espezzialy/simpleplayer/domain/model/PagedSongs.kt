package com.espezzialy.simpleplayer.domain.model

/**
 * Resultado de um pedido à pesquisa iTunes.
 *
 * [apiConsumedCount] é o tamanho do array `results` na resposta JSON **antes** do filtro local
 * (`kind == "song"`). Serve para alinhar o payload ao domínio; a camada de apresentação pode
 * paginar sem depender de `offset` se a API o ignorar.
 */
data class PagedSongs(
    val songs: List<Song>,
    val apiConsumedCount: Int
)
