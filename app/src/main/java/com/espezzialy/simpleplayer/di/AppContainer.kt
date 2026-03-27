package com.espezzialy.simpleplayer.di

import com.espezzialy.simpleplayer.core.coroutines.DefaultDispatcherProvider
import com.espezzialy.simpleplayer.data.remote.api.ItunesApiService
import com.espezzialy.simpleplayer.data.remote.SongsRemoteDataSource
import com.espezzialy.simpleplayer.data.remote.SongsRemoteDataSourceImpl
import com.espezzialy.simpleplayer.data.repository.SongRepositoryImpl
import com.espezzialy.simpleplayer.domain.repository.SongRepository
import com.espezzialy.simpleplayer.domain.usecase.SearchSongsUseCase
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class AppContainer {
    private val dispatchers = DefaultDispatcherProvider()

    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl("https://itunes.apple.com/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val itunesApiService: ItunesApiService =
        retrofit.create(ItunesApiService::class.java)

    private val songsRemoteDataSource: SongsRemoteDataSource =
        SongsRemoteDataSourceImpl(
            apiService = itunesApiService,
            dispatcherProvider = dispatchers
        )

    private val songRepository: SongRepository =
        SongRepositoryImpl(remoteDataSource = songsRemoteDataSource)

    val searchSongsUseCase: SearchSongsUseCase =
        SearchSongsUseCase(repository = songRepository)
}
