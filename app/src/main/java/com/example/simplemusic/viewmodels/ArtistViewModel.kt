package com.example.simplemusic.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.simplemusic.models.Artist
import com.example.simplemusic.repositories.ArtistRepository
import kotlinx.coroutines.launch

class ArtistViewModel : ViewModel() {
    val artists = MutableLiveData<List<Artist>>()
    private val artistRepository = ArtistRepository()

    /*
    init {
        viewModelScope.launch {
            artists.value = artistRepository.getArtists("jack+johnson", 20)
        }
    }
    */

    suspend fun searchArtist(term: String, limit: Int) {
        artists.value = artistRepository.getArtists(term, 20)
    }
}