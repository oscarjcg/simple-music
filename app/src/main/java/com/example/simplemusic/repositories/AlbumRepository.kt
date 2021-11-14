package com.example.simplemusic.repositories


import android.util.Log
import com.example.simplemusic.models.multimediacontent.ArtistAlbum
import com.example.simplemusic.database.dao.ApiCacheDao
import com.example.simplemusic.models.RepositoryResult
import com.example.simplemusic.models.RepositoryResult.Success
import com.example.simplemusic.models.RepositoryResult.Error
import com.example.simplemusic.models.SearchResponse
import com.example.simplemusic.utils.CACHE_INTERVAL_DAYS
import com.example.simplemusic.utils.DAY_MS
import com.example.simplemusic.utils.WRAPPER_TYPE_ARTIST
import com.example.simplemusic.webservices.AlbumWebService
import java.lang.Exception
import java.util.*
import kotlin.collections.ArrayList

private const val PAGINATION = 20

class AlbumRepository(private val apiCacheDao: ApiCacheDao,
                      private val albumWebService: AlbumWebService) {

    var pagination = 0

    suspend fun getArtistAlbums(artistId: Int): RepositoryResult<List<ArtistAlbum>> {
        addPagination()

        // Check cache and use it if available
        val albumsCache = getCache(artistId, pagination)
        if (albumsCache != null)
            return Success(albumsCache)

        // Start request
        val searchResponse: SearchResponse<ArtistAlbum>
        try {
            searchResponse = albumWebService.getArtistAlbums(artistId, pagination)
        } catch (e: Exception) {
            return Error(e)
        }

        // Filter. Only albums
        val albums = ArrayList(searchResponse.results ?: listOf())
        if (albums.isNotEmpty()) {
            if (albums[0].wrapperType.equals(WRAPPER_TYPE_ARTIST))
                albums.removeAt(0)
        }

        // Save to cache
        saveCache(artistId, pagination, albums)

        return Success(albums)
    }

    fun resetPagination() {
        pagination = 0
    }

    private fun addPagination() {
        pagination += PAGINATION
    }

    private suspend fun getCache(artistId: Int, limit: Int): List<ArtistAlbum>? {
        // Check cache and use it if available
        var albumsCache = apiCacheDao.getArtistOwnerAlbums(artistId, 1)
        // Only if it is big enough. All albums have the same limit and owner
        // so the first one is enough to check
        if (albumsCache.isNotEmpty() && limit <= albumsCache[0].limit!!) {
            if (isCacheValid(albumsCache[0].cacheDate!!.time)) {
                albumsCache = apiCacheDao.getArtistOwnerAlbums(artistId, limit)
                return albumsCache
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

    private suspend fun saveCache(artistId: Int, limit: Int, albums: List<ArtistAlbum>) {
        for (i in albums.indices) {
            val album = albums[i]
            album.limit = limit
            album.order = i
            album.cacheDate = Date()
            // This is because an album can be a collaboration, so the artist id could be different
            album.artistIdOwner = artistId
        }
        apiCacheDao.addAllAlbums(albums)
    }

    suspend fun deleteAll() {
        apiCacheDao.deleteAllAlbums()
    }
}
