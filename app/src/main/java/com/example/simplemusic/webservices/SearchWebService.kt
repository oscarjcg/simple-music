package com.example.simplemusic.webservices

import com.example.simplemusic.models.SearchResponse
import com.example.simplemusic.models.multimediacontent.Artist
import com.example.simplemusic.utils.*
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Api for searching artist, albums and songs.
 */
interface SearchWebService {

    @GET("search")
    suspend fun getArtists(
        @Query(PARAM_TERM) term: String,
        @Query(PARAM_LIMIT) limit: Int,
        @Query(PARAM_ENTITY) entity: String = "musicArtist"): SearchResponse<Artist>
}
