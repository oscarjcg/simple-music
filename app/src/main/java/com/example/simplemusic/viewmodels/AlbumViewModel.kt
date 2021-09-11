package com.example.simplemusic.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.simplemusic.models.multimediacontent.ArtistAlbum
import com.example.simplemusic.database.AppDatabase
import com.example.simplemusic.repositories.AlbumRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * View model for albums.
 */
@HiltViewModel
class AlbumViewModel
    @Inject
    constructor(
        private val albumRepository: AlbumRepository
) : ViewModel() {
    val albums = MutableLiveData<List<ArtistAlbum>>()


    // UI
    var searchedArtist: String? = null
    var searchingAlbums: Boolean = false

    suspend fun searchArtistAlbum(artistId: Int, limit: Int) {
        searchingAlbums = true
        albums.value = albumRepository.getArtistAlbums(artistId, limit)
    }

    suspend fun deleteAll() {
        albumRepository.deleteAll()
    }
}