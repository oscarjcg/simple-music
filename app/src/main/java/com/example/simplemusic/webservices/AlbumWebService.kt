package com.example.simplemusic.webservices

import com.example.simplemusic.models.SearchResponse
import com.example.simplemusic.models.multimediacontent.ArtistAlbum
import com.example.simplemusic.utils.*
import retrofit2.http.GET
import retrofit2.http.Query

interface AlbumWebService {
    @GET("lookup")
    suspend fun getArtistAlbums(
        @Query(PARAM_ID) artistId: Int,
        @Query(PARAM_LIMIT) limit: Int,
        @Query(PARAM_ENTITY) entity: String = "album",
        @Query(PARAM_SORT) sort: String = PARAM_SORT_RECENT): SearchResponse<ArtistAlbum>
}
