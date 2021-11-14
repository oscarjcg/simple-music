package com.example.simplemusic.repositories

import com.example.simplemusic.models.multimediacontent.Artist
import com.example.simplemusic.database.dao.ApiCacheDao
import com.example.simplemusic.database.dao.SearchDao
import com.example.simplemusic.models.RepositoryResult
import com.example.simplemusic.models.RepositoryResult.Success
import com.example.simplemusic.models.RepositoryResult.Error
import com.example.simplemusic.models.SearchResponse
import com.example.simplemusic.models.stored.search.Search
import com.example.simplemusic.models.stored.search.SearchResultArtist
import com.example.simplemusic.utils.CACHE_INTERVAL_DAYS
import com.example.simplemusic.utils.DAY_MS
import com.example.simplemusic.webservices.SearchWebService
import java.lang.Exception
import java.util.*
import kotlin.collections.ArrayList

private const val PAGINATION = 20

class ArtistRepository(private val apiCacheDao: ApiCacheDao,
                       private val searchDao: SearchDao,
                       private val searchWebService: SearchWebService) {

    var pagination = 0

    suspend fun getArtists(term: String): RepositoryResult<List<Artist>> {
        addPagination()

        val artistsCache = getCache(term, pagination)
        if (artistsCache != null)
            return Success(artistsCache)

        // Request
        val searchResponse: SearchResponse<Artist>
        try {
            searchResponse = searchWebService.getArtists(term, pagination)
        }
        catch (e: Exception) {
            return Error(e)
        }

        // Filter. Only artist
        val artists = ArrayList(searchResponse.results ?: listOf())

        // Save to cache
        saveCache(term, pagination, artists)

        return Success(artists)
    }

    fun resetPagination() {
        pagination = 0
    }

    private fun addPagination() {
        pagination += PAGINATION
    }

    private suspend fun getCache(term: String, limit: Int): List<Artist>? {
        // Check cache and use it if available
        val search = searchDao.getSearch(term)

        // Only if it is big enough
        if (search != null && limit <= search.limit) {
            if (isCacheValid(search.cacheDate!!.time)) {
                val artistsId = searchDao.getSearchResultsArtistId(search.searchId, limit)

                val artistsCache = ArrayList<Artist>()
                for (id in artistsId)
                    artistsCache.add(apiCacheDao.getArtist(id))
                return artistsCache
            } else {
                deleteAll()
                deleteAllSearch()
            }
        }

        return null
    }

    private fun isCacheValid(cacheTime: Long): Boolean {
        val now = System.currentTimeMillis()
        val cacheDateExpiration = cacheTime + (CACHE_INTERVAL_DAYS * DAY_MS)
        return now < cacheDateExpiration
    }

    private suspend fun saveCache(term: String, limit: Int, artists: List<Artist>) {
        // Add search or update if exits
        var search = searchDao.getSearch(term)

        val searchId: Long
        if (search != null) {
            search.limit = limit
            search.cacheDate = Date()
            searchDao.updateSearch(search)
            searchId = search.searchId
        } else {
            search = Search(term, limit, Date())
            searchId = searchDao.addSearch(search)
        }

        // Add search results
        val results = ArrayList<SearchResultArtist>()
        for (i in artists.indices) {
            val artist = artists[i]
            val searchResultArtist = SearchResultArtist(searchId, artist.artistId!!, i)
            results.add(searchResultArtist)
        }
        searchDao.addSearchResultsArtist(results)

        // Add artists
        apiCacheDao.addAllArtists(artists)
    }

    suspend fun deleteAll() {
        apiCacheDao.deleteAllArtist()
    }

    suspend fun deleteAllSearch() {
        searchDao.deleteAllSearchResults()
        searchDao.deleteAllSearch()
    }

}
