package com.espezzialy.simpleplayer.domain.usecase

import com.espezzialy.simpleplayer.domain.model.Song
import com.espezzialy.simpleplayer.domain.repository.SongRepository

class SearchSongsUseCase(
    private val repository: SongRepository
) {
    suspend operator fun invoke(query: String): List<Song> {
        val normalized = query.trim()
        if (normalized.isBlank()) return emptyList()
        return repository.searchSongs(term = normalized, limit = LIMIT)
    }

    private companion object {
        const val LIMIT = 25
    }
}
