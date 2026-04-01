package com.espezzialy.simpleplayer.core.media

private val ItunesArtworkSizeSuffix = Regex("(\\d+)x(\\d+)bb\\.jpg$")

fun String?.toItunesArtwork600(): String? {
    if (this.isNullOrBlank()) return null
    return trim().replace(ItunesArtworkSizeSuffix, "600x600bb.jpg")
}

fun String?.toItunesArtwork200(): String? {
    if (this.isNullOrBlank()) return null
    return trim().replace(ItunesArtworkSizeSuffix, "200x200bb.jpg")
}
