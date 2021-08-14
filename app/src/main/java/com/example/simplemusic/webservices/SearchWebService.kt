package com.example.simplemusic.webservices

import com.example.simplemusic.models.SearchResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface SearchWebService {

    @GET("search")
    suspend fun getArtists(
        @Query("term") term: String,
        @Query("limit") limit: Int,
        @Query("entity") entity: String = "musicArtist"): SearchResponse

    @GET("lookup")
    suspend fun getArtistAlbums(
        @Query("id") artistId: Int,
        @Query("limit") limit: Int,
        @Query("entity") entity: String = "album"): SearchResponse
}