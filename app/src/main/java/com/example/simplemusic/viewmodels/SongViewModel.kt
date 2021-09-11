package com.example.simplemusic.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.simplemusic.models.multimediacontent.AlbumSong
import com.example.simplemusic.database.AppDatabase
import com.example.simplemusic.repositories.SongRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * View model for songs.
 */
@HiltViewModel
class SongViewModel
    @Inject
    constructor(
        private var songRepository: SongRepository
    ) : ViewModel() {

    val songs = MutableLiveData<List<AlbumSong>>()

    // UI
    var selectedAlbum: String? = null
    var searchingSongs: Boolean = false

    suspend fun searchAlbumSongs(albumId: Long, limit: Int) {
        searchingSongs = true
        songs.value = songRepository.getAlbumSongs(albumId, limit)
    }

    suspend fun deleteAll() {
        songRepository.deleteAll()
    }
}