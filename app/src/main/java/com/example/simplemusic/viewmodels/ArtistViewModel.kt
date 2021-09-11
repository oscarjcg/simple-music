package com.example.simplemusic.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.simplemusic.models.multimediacontent.Artist
import com.example.simplemusic.database.AppDatabase
import com.example.simplemusic.repositories.ArtistRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * View model for artists.
 */
@HiltViewModel
class ArtistViewModel
    @Inject
    constructor(
        private val artistRepository: ArtistRepository
    ) : ViewModel() {
    val artists = MutableLiveData<List<Artist>>()

    // UI
    var searchedArtist: String? = null
    var searchingArtist: Boolean = false

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