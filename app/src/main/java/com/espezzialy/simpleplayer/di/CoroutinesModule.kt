package com.espezzialy.simpleplayer.di

import com.espezzialy.simpleplayer.core.coroutines.DefaultDispatcherProvider
import com.espezzialy.simpleplayer.core.coroutines.DispatcherProvider
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class CoroutinesModule {

    @Binds
    @Singleton
    abstract fun bindDispatcherProvider(
        impl: DefaultDispatcherProvider
    ): DispatcherProvider
}
