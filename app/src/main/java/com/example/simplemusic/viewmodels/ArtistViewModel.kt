package com.example.simplemusic.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.simplemusic.models.multimediacontent.Artist
import com.example.simplemusic.repositories.ArtistRepository

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