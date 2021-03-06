package com.example.simplemusic.repositories

import com.example.simplemusic.models.multimediacontent.MusicVideo
import com.example.simplemusic.database.dao.ApiCacheDao
import com.example.simplemusic.models.RepositoryResult
import com.example.simplemusic.models.RepositoryResult.Success
import com.example.simplemusic.models.RepositoryResult.Error
import com.example.simplemusic.models.SearchResponse
import com.example.simplemusic.utils.CACHE_INTERVAL_DAYS
import com.example.simplemusic.utils.DAY_MS
import com.example.simplemusic.webservices.MusicVideoWebService
import java.lang.Exception
import java.util.*
import kotlin.collections.ArrayList

private const val PAGINATION = 20

class MusicVideoRepository(private val apiCacheDao: ApiCacheDao,
                           private val musicVideoWebService: MusicVideoWebService) {

    var pagination = 0

    suspend fun getArtistMusicVideos(term: String): RepositoryResult<List<MusicVideo>> {
        addPagination()

        // Check cache and use it if available
        val musicVideosCache = getCache(term, pagination)
        if (musicVideosCache != null)
            return Success(musicVideosCache)

        // Request
        val searchResponse: SearchResponse<MusicVideo>
        try {
            searchResponse = musicVideoWebService.getArtistMusicVideos(term, pagination)
        } catch (e: Exception) {
            return Error(e)
        }

        // Filter. Only artist
        val musicVideos = ArrayList(searchResponse.results ?: listOf())

        // Save to cache with cache info
        saveCache(term, pagination, musicVideos)

        return Success(musicVideos)
    }

    fun resetPagination() {
        pagination = 0
    }

    private fun addPagination() {
        pagination += PAGINATION
    }

    private suspend fun getCache(term: String, limit: Int): List<MusicVideo>? {
        // Check cache and use it if available
        var musicVideosCache = apiCacheDao.getArtistOwnerMusicVideos(term, 1)
        // Only if it is big enough. All music videos have the same limit and owner
        // so the first one is enough to check
        if (musicVideosCache.isNotEmpty() && limit <= musicVideosCache[0].limit!!) {
            if (isCacheValid(musicVideosCache[0].cacheDate!!.time)) {
                musicVideosCache = apiCacheDao.getArtistOwnerMusicVideos(term, limit)
                return musicVideosCache
            } else {
                deleteAll()
            }
        }
        return null
    }

    private fun isCacheValid(cacheTime: Long): Boolean {
        val now = System.currentTimeMillis()
        val cacheDateExpiration = cacheTime + (CACHE_INTERVAL_DAYS * DAY_MS)
        return now < cacheDateExpiration
    }


    private suspend fun saveCache(term: String, limit: Int, musicVideos: List<MusicVideo>) {
        // Save to cache with cache info
        for (i in musicVideos.indices) {
            val mv = musicVideos[i]
            mv.limit = limit
            mv.order = i
            // This is because a music video can be a collaboration, so the artist id could be different
            mv.artistOwner = term
            mv.cacheDate = Date()
        }
        apiCacheDao.addAllMusicVideos(musicVideos)
    }

    suspend fun deleteAll() {
        apiCacheDao.deleteAllMusicVideos()
    }

}
