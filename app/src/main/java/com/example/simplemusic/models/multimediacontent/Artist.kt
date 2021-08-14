package com.example.simplemusic.models.multimediacontent

import com.example.simplemusic.models.multimediacontent.MultimediaContent
import com.google.gson.annotations.SerializedName
import com.google.gson.annotations.Expose

class Artist : MultimediaContent() {

    @SerializedName("artistType")
    @Expose
    var artistType: String? = null

    @SerializedName("artistName")
    @Expose
    var artistName: String? = null

    @SerializedName("artistLinkUrl")
    @Expose
    var artistLinkUrl: String? = null

    @SerializedName("artistId")
    @Expose
    var artistId: Int? = null

    @SerializedName("primaryGenreName")
    @Expose
    var primaryGenreName: String? = null

    @SerializedName("primaryGenreId")
    @Expose
    var primaryGenreId: Int? = null
}