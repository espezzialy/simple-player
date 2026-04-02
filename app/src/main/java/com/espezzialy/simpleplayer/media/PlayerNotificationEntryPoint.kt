package com.espezzialy.simpleplayer.media

import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@EntryPoint
@InstallIn(SingletonComponent::class)
interface PlayerNotificationEntryPoint {
    fun playerNotificationController(): PlayerNotificationController
}
