package com.example.simplemusic.repositories

import android.util.Log
import com.example.simplemusic.models.multimediacontent.MusicVideo
import com.example.simplemusic.database.dao.ApiCacheDao
import com.example.simplemusic.models.SearchResponse
import com.example.simplemusic.utils.BASE_URL
import com.example.simplemusic.webservices.SearchWebService
import com.google.gson.GsonBuilder
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.Exception

class MusicVideoRepository(private val apiCacheDao: ApiCacheDao,
                           private val searchWebService: SearchWebService) {

    suspend fun getArtistMusicVideos(term: String, limit: Int): List<MusicVideo> {
        // Check cache and use it if available
        val musicVideosCache = getCache(term, limit)
        if (musicVideosCache != null)
            return musicVideosCache

        // Request
        val searchResponse: SearchResponse
        try {
            searchResponse = searchWebService.getArtistMusicVideos(term, limit)
        } catch (e: Exception) {
            e.printStackTrace()
            // In case of error just return no results
            return ArrayList()
        }

        // Filter. Only artist
        val musicVideos = searchResponse.results?.filterIsInstance<MusicVideo>() as ArrayList<MusicVideo>

        // Save to cache with cache info
        saveCache(term, limit, musicVideos)

        return musicVideos
    }

    private suspend fun getCache(term: String, limit: Int): List<MusicVideo>? {
        // Check cache and use it if available
        var musicVideosCache = apiCacheDao.getArtistOwnerMusicVideos(term, 1)
        // Only if it is big enough. All music videos have the same limit and owner
        // so the first one is enough to check
        if (musicVideosCache.isNotEmpty() && limit <= musicVideosCache[0].limit!!) {
            musicVideosCache = apiCacheDao.getArtistOwnerMusicVideos(term, limit)
            Log.println(Log.ERROR, "DEBUG", "cache ${musicVideosCache.size}")//
            return musicVideosCache
        }
        return null
    }

    private suspend fun saveCache(term: String, limit: Int, musicVideos: List<MusicVideo>) {
        // Save to cache with cache info
        for (i in musicVideos.indices) {
            val mv = musicVideos[i]
            mv.limit = limit
            mv.order = i
            // This is because a music video can be a collaboration, so the artist id could be different
            mv.artistOwner = term
        }
        apiCacheDao.addAllMusicVideos(musicVideos)
    }

    suspend fun deleteAll() {
        apiCacheDao.deleteAllMusicVideos()
    }

}