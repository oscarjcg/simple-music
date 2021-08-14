package com.example.simplemusic.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.simplemusic.models.multimediacontent.ArtistAlbum
import com.example.simplemusic.repositories.AlbumRepository

class AlbumViewModel : ViewModel() {
    val albums = MutableLiveData<List<ArtistAlbum>>()
    private val albumRepository = AlbumRepository()

    suspend fun searchArtistAlbum(artistId: Int, limit: Int) {
        albums.value = albumRepository.getArtistAlbums(artistId, limit)
    }
}