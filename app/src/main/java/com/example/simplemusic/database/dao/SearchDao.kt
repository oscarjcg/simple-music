package com.example.simplemusic.database.dao

import androidx.room.*
import com.example.simplemusic.models.stored.search.Search
import com.example.simplemusic.models.stored.search.SearchResultArtist

@Dao
interface SearchDao {
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

    @Query("DELETE FROM search_result_artist")
    suspend fun deleteAllSearchResults()

    @Query("DELETE FROM search")
    suspend fun deleteAllSearch()
}