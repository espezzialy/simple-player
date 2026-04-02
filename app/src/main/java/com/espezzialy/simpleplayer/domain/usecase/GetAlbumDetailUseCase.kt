package com.espezzialy.simpleplayer.domain.usecase

import com.espezzialy.simpleplayer.domain.model.AlbumDetail
import com.espezzialy.simpleplayer.domain.repository.SongRepository
import javax.inject.Inject

class GetAlbumDetailUseCase
    @Inject
    constructor(
        private val repository: SongRepository,
    ) {
        suspend operator fun invoke(collectionId: Long): AlbumDetail {
            return repository.getAlbumDetail(collectionId = collectionId)
        }
    }
