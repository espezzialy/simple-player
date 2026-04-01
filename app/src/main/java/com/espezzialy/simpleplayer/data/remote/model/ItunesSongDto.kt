package com.espezzialy.simpleplayer.data.remote.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class ItunesSongDto(
    @SerializedName("wrapperType")
    val wrapperType: String? = null,
    @SerializedName("kind")
    val kind: String? = null,
    @SerializedName("artistId")
    val artistId: Long? = null,
    @SerializedName("collectionId")
    val collectionId: Long? = null,
    @SerializedName("trackId")
    val trackId: Long? = null,
    @SerializedName("artistName")
    val artistName: String? = null,
    @SerializedName("collectionName")
    val collectionName: String? = null,
    @SerializedName("trackName")
    val trackName: String? = null,
    @SerializedName("collectionCensoredName")
    val collectionCensoredName: String? = null,
    @SerializedName("trackCensoredName")
    val trackCensoredName: String? = null,
    @SerializedName("artistViewUrl")
    val artistViewUrl: String? = null,
    @SerializedName("collectionViewUrl")
    val collectionViewUrl: String? = null,
    @SerializedName("trackViewUrl")
    val trackViewUrl: String? = null,
    @SerializedName("previewUrl")
    val previewUrl: String? = null,
    @SerializedName("artworkUrl30")
    val artworkUrl30: String? = null,
    @SerializedName("artworkUrl60")
    val artworkUrl60: String? = null,
    @SerializedName("artworkUrl100")
    val artworkUrl100: String? = null,
    @SerializedName("artworkUrl600")
    val artworkUrl600: String? = null,
    @SerializedName("collectionPrice")
    val collectionPrice: Double? = null,
    @SerializedName("trackPrice")
    val trackPrice: Double? = null,
    @SerializedName("releaseDate")
    val releaseDate: String? = null,
    @SerializedName("collectionExplicitness")
    val collectionExplicitness: String? = null,
    @SerializedName("trackExplicitness")
    val trackExplicitness: String? = null,
    @SerializedName("discCount")
    val discCount: Int? = null,
    @SerializedName("discNumber")
    val discNumber: Int? = null,
    @SerializedName("trackCount")
    val trackCount: Int? = null,
    @SerializedName("trackNumber")
    val trackNumber: Int? = null,
    @SerializedName("trackTimeMillis")
    val trackTimeMillis: Long? = null,
    @SerializedName("country")
    val country: String? = null,
    @SerializedName("currency")
    val currency: String? = null,
    @SerializedName("primaryGenreName")
    val primaryGenreName: String? = null,
    @SerializedName("isStreamable")
    val isStreamable: Boolean? = null,
    @SerializedName("collectionArtistName")
    val collectionArtistName: String? = null,
    @SerializedName("collectionArtistId")
    val collectionArtistId: Long? = null,
    @SerializedName("contentAdvisoryRating")
    val contentAdvisoryRating: String? = null,
    @SerializedName("copyright")
    val copyright: String? = null,
    @SerializedName("description")
    val description: String? = null,
) : Serializable
