package com.example.simplemusic.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.example.simplemusic.models.multimediacontent.Artist
import com.example.simplemusic.database.AppDatabase
import com.example.simplemusic.repositories.ArtistRepository

/**
 * View model for artists.
 */
class ArtistViewModel(application: Application) : AndroidViewModel(application) {
    val artists = MutableLiveData<List<Artist>>()
    private val artistRepository: ArtistRepository

    // UI
    var searchedArtist: String? = null
    var searchingArtist: Boolean = false

    init {
        val db = AppDatabase.getDatabase(application)
        artistRepository = ArtistRepository(db.apiCacheDao(), db.searchDao())
    }

    suspend fun searchArtist(term: String, limit: Int) {
        searchedArtist = term
        searchingArtist = true
        artists.value = artistRepository.getArtists(term, limit)
    }

    suspend fun deleteAll() {
        artistRepository.deleteAll()
        artistRepository.deleteAllSearch()
    }
}