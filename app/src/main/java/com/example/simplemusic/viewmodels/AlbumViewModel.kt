package com.example.simplemusic.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.example.simplemusic.models.multimediacontent.ArtistAlbum
import com.example.simplemusic.database.AppDatabase
import com.example.simplemusic.repositories.AlbumRepository

/**
 * View model for albums.
 */
class AlbumViewModel(application: Application) : AndroidViewModel(application) {
    val albums = MutableLiveData<List<ArtistAlbum>>()
    private val albumRepository: AlbumRepository

    init {
        val db = AppDatabase.getDatabase(application)
        albumRepository = AlbumRepository(db.apiCacheDao())
    }

    // UI
    var searchedArtist: String? = null
    var searchingAlbums: Boolean = false

    suspend fun searchArtistAlbum(artistId: Int, limit: Int) {
        searchingAlbums = true
        albums.value = albumRepository.getArtistAlbums(artistId, limit)
    }
}