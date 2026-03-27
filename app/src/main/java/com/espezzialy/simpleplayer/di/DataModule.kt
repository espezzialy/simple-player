package com.espezzialy.simpleplayer.di

import com.espezzialy.simpleplayer.data.remote.SongsRemoteDataSource
import com.espezzialy.simpleplayer.data.remote.SongsRemoteDataSourceImpl
import com.espezzialy.simpleplayer.data.repository.SongRepositoryImpl
import com.espezzialy.simpleplayer.domain.repository.SongRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DataModule {

    @Binds
    @Singleton
    abstract fun bindSongsRemoteDataSource(
        impl: SongsRemoteDataSourceImpl
    ): SongsRemoteDataSource

    @Binds
    @Singleton
    abstract fun bindSongRepository(
        impl: SongRepositoryImpl
    ): SongRepository
}
