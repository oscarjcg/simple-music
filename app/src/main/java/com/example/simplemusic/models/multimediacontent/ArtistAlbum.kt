package com.example.simplemusic.models.multimediacontent

import com.example.simplemusic.models.multimediacontent.MultimediaContent
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.util.*

class ArtistAlbum: MultimediaContent() {

    @SerializedName("collectionName")
    @Expose
    var collectionName: String? = null

    @SerializedName("collectionId")
    @Expose
    var collectionId: Int? = null

    @SerializedName("artworkUrl100")
    @Expose
    var artworkUrl100: String? = null

    @SerializedName("releaseDate")
    @Expose
    var releaseDate: Date? = null

}