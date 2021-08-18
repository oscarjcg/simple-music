package com.example.simplemusic.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.simplemusic.models.multimediacontent.Artist
import com.example.simplemusic.repositories.ArtistRepository

/**
 * View model for artists.
 */
class ArtistViewModel : ViewModel() {
    val artists = MutableLiveData<List<Artist>>()
    private val artistRepository = ArtistRepository()

    // UI
    var searchedArtist: String? = null
    var searchingArtist: Boolean = false

    suspend fun searchArtist(term: String, limit: Int) {
        searchedArtist = term
        searchingArtist = true
        artists.value = artistRepository.getArtists(term, limit)
    }
}