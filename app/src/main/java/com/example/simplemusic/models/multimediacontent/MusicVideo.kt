package com.example.simplemusic.models.multimediacontent

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.util.*

/**
 * Music video data structure.
 */
class MusicVideo : MultimediaContent() {
    @SerializedName("trackId")
    @Expose
    var trackId: Long? = null

    @SerializedName("trackName")
    @Expose
    var trackName: String? = null

    @SerializedName("previewUrl")
    @Expose
    var previewUrl: String? = null

    @SerializedName("artworkUrl100")
    @Expose
    var artworkUrl100: String? = null

    @SerializedName("releaseDate")
    @Expose
    var releaseDate: Date? = null
}