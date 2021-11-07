package com.example.simplemusic.repositories

import android.util.Log
import com.example.simplemusic.models.multimediacontent.ArtistAlbum
import com.example.simplemusic.database.dao.ApiCacheDao
import com.example.simplemusic.models.RepositoryResult
import com.example.simplemusic.models.RepositoryResult.Success
import com.example.simplemusic.models.RepositoryResult.Error
import com.example.simplemusic.models.SearchResponse
import com.example.simplemusic.webservices.AlbumWebService
import java.lang.Exception

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
        val searchResponse: SearchResponse
        try {
            searchResponse = albumWebService.getArtistAlbums(artistId, pagination)
        } catch (e: Exception) {
            return Error(e)
        }

        // Filter. Only albums
        val albums = searchResponse.results?.filterIsInstance<ArtistAlbum>() as ArrayList<ArtistAlbum>
        //Log.println(Log.ERROR, "DEBUG", "request ${albums.size}")//

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
            albumsCache = apiCacheDao.getArtistOwnerAlbums(artistId, limit)
            //Log.println(Log.ERROR, "DEBUG", "cache ${albumsCache.size}")//
            return albumsCache
        }

        return null
    }

    private suspend fun saveCache(artistId: Int, limit: Int, albums: List<ArtistAlbum>) {
        for (i in albums.indices) {
            val album = albums[i]
            album.limit = limit
            album.order = i
            // This is because an album can be a collaboration, so the artist id could be different
            album.artistIdOwner = artistId
        }
        apiCacheDao.addAllAlbums(albums)
    }

    suspend fun deleteAll() {
        apiCacheDao.deleteAllAlbums()
    }
}
