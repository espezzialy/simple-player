package com.espezzialy.simpleplayer.domain.model

/**
 * Modelo de domínio para exibição e regras de negócio.
 * O [ItunesSongDto] permanece na camada de dados com todos os campos da API.
 */
data class Song(
    val trackId: Long,
    val trackName: String,
    val artistName: String,
    val collectionName: String,
    /** ID da coleção na iTunes (lookup do álbum). */
    val collectionId: Long?,
    /** URL da capa 100×100 (campo `artworkUrl100` da API). */
    val artworkUrl100: String?
)
