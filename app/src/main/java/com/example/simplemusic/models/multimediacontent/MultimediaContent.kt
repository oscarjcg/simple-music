package com.example.simplemusic.models.multimediacontent

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * Wrapper data structure.
 */
open class MultimediaContent {
    @SerializedName("wrapperType")
    @Expose
    var wrapperType: String? = null
}
