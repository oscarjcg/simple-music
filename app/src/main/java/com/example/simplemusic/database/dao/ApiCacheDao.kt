package com.example.simplemusic.database.dao

import androidx.room.*
import com.example.simplemusic.models.multimediacontent.AlbumSong
import com.example.simplemusic.models.multimediacontent.Artist
import com.example.simplemusic.models.multimediacontent.ArtistAlbum
import com.example.simplemusic.models.multimediacontent.MusicVideo
import com.example.simplemusic.models.stored.search.Search
import com.example.simplemusic.models.stored.search.SearchResultArtist

@Dao
interface ApiCacheDao {

    // Artist
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addAllArtists(artists: List<Artist>)

    @Query("SELECT * FROM artist WHERE artistId IN (:artistsId)")
    suspend fun getArtists(artistsId: List<Int>) : List<Artist>

    @Query("SELECT * FROM artist WHERE artistId = :artistId")
    suspend fun getArtist(artistId: Int) : Artist

    // Search
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addSearch(search: Search): Long

    @Update
    suspend fun updateSearch(search: Search)

    @Query("SELECT * FROM search WHERE term = :term")
    suspend fun getSearch(term: String): Search

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addSearchResultsArtist(searchResultsArtist: List<SearchResultArtist>)

    @Transaction
    @Query("SELECT artistId FROM search_result_artist WHERE searchId = :searchId ORDER BY `order` LIMIT :limit")
    suspend fun getSearchResultsArtistId(searchId: Long, limit: Int): List<Int>

    // Albums
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addAllAlbums(albums: List<ArtistAlbum>)

    @Query("DELETE FROM album")
    suspend fun deleteAllAlbums()

    @Query("SELECT * FROM album WHERE artistIdOwner = :artistIdOwner ORDER BY `order` LIMIT :limit")
    suspend fun getArtistOwnerAlbums(artistIdOwner: Int, limit: Int): List<ArtistAlbum>

    // Songs
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addAllSongs(songs: List<AlbumSong>)

    @Query("DELETE FROM song")
    suspend fun deleteAllSongs()

    @Query("SELECT * FROM song WHERE collectionIdOwner = :collectionIdOwner ORDER BY `order` LIMIT :limit")
    suspend fun getAlbumOwnerSongs(collectionIdOwner: Long, limit: Int): List<AlbumSong>

    // Music videos
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addAllMusicVideos(musicVideos: List<MusicVideo>)

    @Query("DELETE FROM music_video")
    suspend fun deleteAllMusicVideos()

    @Query("SELECT * FROM music_video WHERE artistOwner = :artistOwner ORDER BY `order` LIMIT :limit")
    suspend fun getArtistOwnerMusicVideos(artistOwner: String, limit: Int): List<MusicVideo>
}