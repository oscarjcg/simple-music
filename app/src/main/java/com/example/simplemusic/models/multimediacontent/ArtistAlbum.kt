package com.example.simplemusic.models.multimediacontent

import com.example.simplemusic.models.multimediacontent.MultimediaContent
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class ArtistAlbum: MultimediaContent() {

    @SerializedName("collectionName")
    @Expose
    var collectionName: String? = null

    @SerializedName("collectionId")
    @Expose
    var collectionId: Int? = null

}