package com.example.simplemusic.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.simplemusic.models.multimediacontent.ArtistAlbum
import com.example.simplemusic.repositories.AlbumRepository

class AlbumViewModel : ViewModel() {
    val albums = MutableLiveData<List<ArtistAlbum>>()
    private val albumRepository = AlbumRepository()

    // UI
    var searchedArtist: String? = null
    var searchingAlbums: Boolean = false

    suspend fun searchArtistAlbum(artistId: Int, limit: Int) {
        searchingAlbums = true
        albums.value = albumRepository.getArtistAlbums(artistId, limit)
    }
}