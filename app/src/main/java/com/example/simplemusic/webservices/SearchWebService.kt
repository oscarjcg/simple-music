package com.example.simplemusic.webservices

import com.example.simplemusic.models.SearchResponse
import retrofit2.http.GET
import retrofit2.http.Query

private const val PARAM_TERM = "term"
private const val PARAM_LIMIT = "limit"
private const val PARAM_ENTITY = "entity"
private const val PARAM_ID = "id"
private const val PARAM_ATTRIBUTE = "attribute"

/**
 * Api for searching artist, albums and songs.
 */
interface SearchWebService {

    @GET("search")
    suspend fun getArtists(
        @Query(PARAM_TERM) term: String,
        @Query(PARAM_LIMIT) limit: Int,
        @Query(PARAM_ENTITY) entity: String = "musicArtist"): SearchResponse

    @GET("lookup")
    suspend fun getArtistAlbums(
        @Query(PARAM_ID) artistId: Int,
        @Query(PARAM_LIMIT) limit: Int,
        @Query(PARAM_ENTITY) entity: String = "album"): SearchResponse

    @GET("lookup")
    suspend fun getAlbumSongs(
        @Query(PARAM_ID) artistId: Int,
        @Query(PARAM_LIMIT) limit: Int,
        @Query(PARAM_ENTITY) entity: String = "song"): SearchResponse

    @GET("search")
    suspend fun getArtistMusicVideos(
        @Query(PARAM_TERM) term: String,
        @Query(PARAM_LIMIT) limit: Int,
        @Query(PARAM_ENTITY) entity: String = "musicVideo",
        @Query(PARAM_ATTRIBUTE) attribute: String = "allArtistTerm"): SearchResponse
}