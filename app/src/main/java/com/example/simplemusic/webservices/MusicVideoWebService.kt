package com.example.simplemusic.webservices

import com.example.simplemusic.models.SearchResponse
import com.example.simplemusic.models.multimediacontent.MusicVideo
import com.example.simplemusic.utils.*
import retrofit2.http.GET
import retrofit2.http.Query

interface MusicVideoWebService {
    @GET("search")
    suspend fun getArtistMusicVideos(
        @Query(PARAM_TERM) term: String,
        @Query(PARAM_LIMIT) limit: Int,
        @Query(PARAM_ENTITY) entity: String = "musicVideo",
        @Query(PARAM_ATTRIBUTE) attribute: String = "allArtistTerm",
        @Query(PARAM_SORT) sort: String = PARAM_SORT_RECENT
    ): SearchResponse<MusicVideo>
}
