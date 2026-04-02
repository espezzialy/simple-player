package com.espezzialy.simpleplayer.core.player

import com.espezzialy.simpleplayer.domain.model.Song

/** Next/previous index in [queue], with optional wrap from last to first when [repeatPlaylist]. */
fun computeSkipTargetIndex(
    queue: List<Song>,
    currentTrackId: Long,
    delta: Int,
    repeatPlaylist: Boolean,
): Int? {
    if (queue.isEmpty()) return null
    val idx = queue.indexOfFirst { it.trackId == currentTrackId }
    if (idx < 0) return null
    val newIdx =
        when {
            delta > 0 && idx == queue.lastIndex && repeatPlaylist -> 0
            else -> idx + delta
        }
    return newIdx.takeIf { it in queue.indices }
}
