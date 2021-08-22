package com.example.simplemusic.repositories

import android.util.Log
import com.example.simplemusic.models.multimediacontent.AlbumSong
import com.example.simplemusic.database.dao.ApiCacheDao
import com.example.simplemusic.models.SearchResponse
import com.example.simplemusic.utils.BASE_URL
import com.example.simplemusic.webservices.SearchWebService
import com.google.gson.GsonBuilder
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.Exception

class SongRepository(private val apiCacheDao: ApiCacheDao) {

    suspend fun getAlbumSongs(albumId: Long, limit: Int): List<AlbumSong> {
        // Check cache and use it if available
        var songsCache = getCache(albumId, limit)
        if (songsCache != null)
            return songsCache

        // Start request
        val searchResponse: SearchResponse
        try {
            searchResponse = getRetrofit().create(SearchWebService::class.java).getAlbumSongs(albumId, limit)
        } catch (e: Exception) {
            e.printStackTrace()
            // In case of error just return no results
            return ArrayList()
        }

        // Filter. Only songs
        val songs = searchResponse.results?.filterIsInstance<AlbumSong>() as ArrayList<AlbumSong>
        Log.println(Log.ERROR, "DEBUG", "request ${songs.size}")//

        // Save to cache with cache info
        saveCache(albumId, limit, songs)

        return songs
    }

    private suspend fun getCache(albumId: Long, limit: Int): List<AlbumSong>?  {
        // Check cache and use it if available
        var songsCache = apiCacheDao.getAlbumOwnerSongs(albumId, 1)
        // Only if it is big enough. All songs have the same limit and owner
        // so the first one is enough to check
        if (songsCache.isNotEmpty() && limit <= songsCache[0].limit!!) {
            songsCache = apiCacheDao.getAlbumOwnerSongs(albumId, limit)
            Log.println(Log.ERROR, "DEBUG", "cache ${songsCache.size}")//
            return songsCache
        }
        return null
    }

    private suspend fun saveCache(albumId: Long, limit: Int, songs: List<AlbumSong>) {
        // Save to cache with cache info
        for (i in songs.indices) {
            val song = songs[i]
            song.limit = limit
            song.order = i
            // This is because a song can be a collaboration, so the artist id could be different
            song.collectionIdOwner = albumId
        }
        apiCacheDao.addAllSongs(songs)
    }

    suspend fun deleteAll() {
        apiCacheDao.deleteAllSongs()
    }

    private fun getRetrofit(): Retrofit {
        val gson = GsonBuilder()
            .create()

        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }
}