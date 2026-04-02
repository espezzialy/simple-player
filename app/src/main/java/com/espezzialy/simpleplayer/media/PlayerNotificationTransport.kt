package com.espezzialy.simpleplayer.media

class PlayerNotificationTransport {
    var onSkipPrevious: (() -> Unit)? = null
    var onSkipNext: (() -> Unit)? = null
    var onPlayFromNotification: (() -> Unit)? = null
    var onPauseFromNotification: (() -> Unit)? = null

    var onSeekFromNotification: ((Float) -> Unit)? = null

    fun clear() {
        onSkipPrevious = null
        onSkipNext = null
        onPlayFromNotification = null
        onPauseFromNotification = null
        onSeekFromNotification = null
    }
}
