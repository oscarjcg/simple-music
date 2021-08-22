package com.example.simplemusic.repositories

import android.util.Log
import com.example.simplemusic.models.multimediacontent.Artist
import com.example.simplemusic.database.dao.ApiCacheDao
import com.example.simplemusic.database.dao.SearchDao
import com.example.simplemusic.models.SearchResponse
import com.example.simplemusic.models.stored.search.Search
import com.example.simplemusic.models.stored.search.SearchResultArtist
import com.example.simplemusic.utils.BASE_URL
import com.example.simplemusic.webservices.SearchWebService
import com.google.gson.GsonBuilder
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.Exception

class ArtistRepository(private val apiCacheDao: ApiCacheDao,
                       private val searchDao: SearchDao) {

    suspend fun getArtists(term: String, limit: Int): List<Artist> {
        val artistsCache = getCache(term, limit)
        if (artistsCache != null)
            return artistsCache

        // Request
        val searchResponse: SearchResponse
        try {
            searchResponse = getRetrofit().create(SearchWebService::class.java).getArtists(term, limit)
        }
        catch (e: Exception) {
            e.printStackTrace()
            // In case of error just return no results
            return ArrayList()
        }

        // Filter. Only artist
        val artists = searchResponse.results?.filterIsInstance<Artist>() as ArrayList<Artist>
        Log.println(Log.ERROR, "DEBUG", "request ${artists.size}")//

        // Save to cache
        saveCache(term, limit, artists)

        return artists
    }

    private suspend fun getCache(term: String, limit: Int): List<Artist>? {
        // Check cache and use it if available
        val search = searchDao.getSearch(term)

        // Only if it is big enough
        if (search != null && limit <= search.limit) {
            val artistsId = searchDao.getSearchResultsArtistId(search.searchId, limit)
            Log.println(Log.ERROR, "DEBUG", "cache ${artistsId.size}")//

            val artistsCache = ArrayList<Artist>()
            for (id in artistsId)
                artistsCache.add(apiCacheDao.getArtist(id))
            //artistsCache = apiCacheDao.getArtists(artistsId)
            return artistsCache
        }

        return null
    }

    private suspend fun saveCache(term: String, limit: Int, artists: List<Artist>) {
        // Add search or update if exits
        var search = searchDao.getSearch(term)

        val searchId: Long
        if (search != null) {
            search.limit = limit
            searchDao.updateSearch(search)
            searchId = search.searchId
        } else {
            search = Search(term, limit)
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

    private fun getRetrofit(): Retrofit {
        val gson = GsonBuilder()
            .create()

        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }
}