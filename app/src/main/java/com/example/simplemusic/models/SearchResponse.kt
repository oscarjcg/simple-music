package com.example.simplemusic.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class SearchResponse {
    @SerializedName("resultCount")
    @Expose
    var resultCount: Int? = null

    @SerializedName("results")
    @Expose
    var results: List<Artist>? = null
}