package com.espezzialy.simpleplayer.core.media

/**
 * A CDN da iTunes usa sufixos `NxNbb.jpg` (ex.: 100×100). Trocar por `600x600bb.jpg` melhora a
 * qualidade nas imagens grandes sem novo campo na API (iTunes Search API).
 */
fun String?.toItunesArtwork600(): String? {
    if (this.isNullOrBlank()) return null
    return trim().replace(Regex("(\\d+)x(\\d+)bb\\.jpg$"), "600x600bb.jpg")
}
