package com.example.simplemusic.repositories

import com.example.simplemusic.models.multimediacontent.AlbumSong
import com.example.simplemusic.database.dao.ApiCacheDao
import com.example.simplemusic.models.RepositoryResult
import com.example.simplemusic.models.RepositoryResult.Success
import com.example.simplemusic.models.RepositoryResult.Error
import com.example.simplemusic.models.SearchResponse
import com.example.simplemusic.utils.CACHE_INTERVAL_DAYS
import com.example.simplemusic.utils.DAY_MS
import com.example.simplemusic.utils.WRAPPER_TYPE_COLLECTION
import com.example.simplemusic.webservices.SongWebService
import java.lang.Exception
import java.util.*
import kotlin.collections.ArrayList

private const val PAGINATION = 20

class SongRepository(private val apiCacheDao: ApiCacheDao,
                     private val songWebService: SongWebService) {

    var pagination = 0

    suspend fun getAlbumSongs(albumId: Long): RepositoryResult<List<AlbumSong>> {
        addPagination()

        // Check cache and use it if available
        val songsCache = getCache(albumId, pagination)
        if (songsCache != null)
            return Success(songsCache)

        // Start request
        val searchResponse: SearchResponse<AlbumSong>
        try {
            searchResponse = songWebService.getAlbumSongs(albumId, pagination)
        } catch (e: Exception) {
            return Error(e)
        }

        // Filter. Only songs
        val songs = ArrayList(searchResponse.results ?: listOf())
        if (songs.isNotEmpty()) {
            if (songs[0].wrapperType.equals(WRAPPER_TYPE_COLLECTION))
                songs.removeAt(0)
        }

        // Save to cache with cache info
        saveCache(albumId, pagination, songs)

        return Success(songs)
    }

    fun resetPagination() {
        pagination = 0
    }

    private fun addPagination() {
        pagination += PAGINATION
    }

    private suspend fun getCache(albumId: Long, limit: Int): List<AlbumSong>?  {
        // Check cache and use it if available
        var songsCache = apiCacheDao.getAlbumOwnerSongs(albumId, 1)
        // Only if it is big enough. All songs have the same limit and owner
        // so the first one is enough to check
        if (songsCache.isNotEmpty() && limit <= songsCache[0].limit!!) {
            if (isCacheValid(songsCache[0].cacheDate!!.time)) {
                songsCache = apiCacheDao.getAlbumOwnerSongs(albumId, limit)
                return songsCache
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

    private suspend fun saveCache(albumId: Long, limit: Int, songs: List<AlbumSong>) {
        // Save to cache with cache info
        for (i in songs.indices) {
            val song = songs[i]
            song.limit = limit
            song.order = i
            // This is because a song can be a collaboration, so the artist id could be different
            song.collectionIdOwner = albumId
            song.cacheDate = Date()
        }
        apiCacheDao.addAllSongs(songs)
    }

    suspend fun deleteAll() {
        apiCacheDao.deleteAllSongs()
    }

}
