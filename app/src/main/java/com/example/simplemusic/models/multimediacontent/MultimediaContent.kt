package com.example.simplemusic.models.multimediacontent

import com.example.simplemusic.utils.json.MultimediaContentJsonAdapter
import com.google.gson.annotations.Expose
import com.google.gson.annotations.JsonAdapter
import com.google.gson.annotations.SerializedName

@JsonAdapter(MultimediaContentJsonAdapter::class)
open class MultimediaContent {
    @SerializedName("wrapperType")
    @Expose
    var wrapperType: String? = null
}