package com.espezzialy.simpleplayer.di

import com.espezzialy.simpleplayer.data.remote.api.ItunesApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    private const val ITUNES_BASE_URL = "https://itunes.apple.com/"

    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit =
        Retrofit.Builder()
            .baseUrl(ITUNES_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    @Provides
    @Singleton
    fun provideItunesApiService(retrofit: Retrofit): ItunesApiService =
        retrofit.create(ItunesApiService::class.java)
}
