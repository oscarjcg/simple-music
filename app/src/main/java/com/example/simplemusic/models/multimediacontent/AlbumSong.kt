package com.example.simplemusic.models.multimediacontent

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * Song data structure.
 */
class AlbumSong : MultimediaContent() {
    @SerializedName("trackId")
    @Expose
    var trackId: Long? = null

    @SerializedName("trackName")
    @Expose
    var trackName: String? = null

    @SerializedName("previewUrl")
    @Expose
    var previewUrl: String? = null

    // Helper
    @SerializedName("_like")
    var like: Boolean? = null
}