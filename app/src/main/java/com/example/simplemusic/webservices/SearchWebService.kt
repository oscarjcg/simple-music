package com.example.simplemusic.webservices

import com.example.simplemusic.models.Artist
import com.example.simplemusic.models.SearchResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface SearchWebService {

    @GET("search")
    suspend fun getArtists(
        @Query("term") term: String,
        @Query("limit") limit: Int,
        @Query("entity") entity: String = "musicArtist"): SearchResponse

}