package com.example.simplemusic.repositories

import android.util.Log
import com.example.simplemusic.models.multimediacontent.ArtistAlbum
import com.example.simplemusic.database.dao.ApiCacheDao
import com.example.simplemusic.models.SearchResponse
import com.example.simplemusic.webservices.SearchWebService
import java.lang.Exception

class AlbumRepository(private val apiCacheDao: ApiCacheDao,
                      private val searchWebService: SearchWebService) {

    suspend fun getArtistAlbums(artistId: Int, limit: Int): List<ArtistAlbum> {

        // Check cache and use it if available
        var albumsCache = getCache(artistId, limit)
        if (albumsCache != null)
            return albumsCache

        // Start request
        val searchResponse: SearchResponse
        try {
            searchResponse = searchWebService.getArtistAlbums(artistId, limit)
        } catch (e: Exception) {
            e.printStackTrace()
            // In case of error just return no results
            return ArrayList()
        }

        // Filter. Only albums
        val albums = searchResponse.results?.filterIsInstance<ArtistAlbum>() as ArrayList<ArtistAlbum>
        Log.println(Log.ERROR, "DEBUG", "request ${albums.size}")//

        // Save to cache
        saveCache(artistId, limit, albums)

        return albums
    }

    private suspend fun getCache(artistId: Int, limit: Int): List<ArtistAlbum>? {
        // Check cache and use it if available
        var albumsCache = apiCacheDao.getArtistOwnerAlbums(artistId, 1)
        // Only if it is big enough. All albums have the same limit and owner
        // so the first one is enough to check
        if (albumsCache.isNotEmpty() && limit <= albumsCache[0].limit!!) {
            albumsCache = apiCacheDao.getArtistOwnerAlbums(artistId, limit)
            Log.println(Log.ERROR, "DEBUG", "cache ${albumsCache.size}")//
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