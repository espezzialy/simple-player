package com.espezzialy.simpleplayer.domain.usecase

import com.espezzialy.simpleplayer.domain.model.PagedSongs
import com.espezzialy.simpleplayer.domain.repository.SongRepository
import javax.inject.Inject

class SearchSongsUseCase @Inject constructor(
    private val repository: SongRepository
) {
    /**
     * Pesquisa na iTunes Search API.
     *
     * @param limit Tamanho máximo do lote (ex.: [PAGE_SIZE] na primeira pesquisa, [MAX_ITUNES_TOTAL] ao expandir).
     * @param offset Mantido para o repositório; a API costuma ignorar — a paginação na UI é em duas fases
     * (25 rápidos + expansão) e depois em memória.
     */
    suspend operator fun invoke(query: String, limit: Int = PAGE_SIZE, offset: Int = 0): PagedSongs {
        val normalized = query.trim()
        if (normalized.isBlank()) {
            return PagedSongs(songs = emptyList(), apiConsumedCount = 0)
        }
        val remaining = (MAX_ITUNES_TOTAL - offset).coerceAtLeast(0)
        if (remaining == 0) {
            return PagedSongs(songs = emptyList(), apiConsumedCount = 0)
        }
        val effectiveLimit = minOf(limit, remaining)
        return repository.searchSongs(term = normalized, limit = effectiveLimit, offset = offset)
    }

    companion object {
        const val PAGE_SIZE = 25
        const val MAX_ITUNES_TOTAL = 200
    }
}
