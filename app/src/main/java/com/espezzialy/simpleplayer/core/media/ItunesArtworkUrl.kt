package com.espezzialy.simpleplayer.core.media

/**
 * The iTunes CDN uses `NxNbb.jpg` suffixes (e.g. 100×100). Replacing with `600x600bb.jpg` improves
 * quality for large images without a new API field (iTunes Search API).
 */
fun String?.toItunesArtwork600(): String? {
    if (this.isNullOrBlank()) return null
    return trim().replace(Regex("(\\d+)x(\\d+)bb\\.jpg$"), "600x600bb.jpg")
}
