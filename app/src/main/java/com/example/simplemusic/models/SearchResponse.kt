package com.example.simplemusic.models

import com.example.simplemusic.models.multimediacontent.MultimediaContent
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * Response data structure for the search API.
 */
class SearchResponse {
    @SerializedName("resultCount")
    @Expose
    var resultCount: Int? = null

    @SerializedName("results")
    @Expose
    var results: List<MultimediaContent>? = null
}