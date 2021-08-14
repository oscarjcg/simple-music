package com.example.simplemusic.models.multimediacontent

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class AlbumSong : MultimediaContent() {
    @SerializedName("trackName")
    @Expose
    var trackName: String? = null
}