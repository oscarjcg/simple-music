package com.example.simplemusic.webservices

import com.example.simplemusic.models.SearchResponse
import com.example.simplemusic.models.multimediacontent.AlbumSong
import com.example.simplemusic.utils.PARAM_ENTITY
import com.example.simplemusic.utils.PARAM_ID
import com.example.simplemusic.utils.PARAM_LIMIT
import retrofit2.http.GET
import retrofit2.http.Query

interface SongWebService {
    @GET("lookup")
    suspend fun getAlbumSongs(
        @Query(PARAM_ID) albumId: Long,
        @Query(PARAM_LIMIT) limit: Int,
        @Query(PARAM_ENTITY) entity: String = "song"): SearchResponse<AlbumSong>
}
