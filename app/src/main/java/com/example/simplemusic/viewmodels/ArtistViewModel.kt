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
    var anim = false

    suspend fun searchArtist(term: String) {
        searchedArtist = term
        searchingArtist = true
        artists.value = artistRepository.getArtists(term)
    }

    suspend fun deleteAll() {
        artistRepository.deleteAll()
        artistRepository.deleteAllSearch()
    }

    fun resetPagination() {
        artistRepository.resetPagination()
    }

    fun canGetMoreData(): Boolean {
        val size = artists.value?.size ?: Int.MAX_VALUE
        return artistRepository.pagination <= size
    }
}