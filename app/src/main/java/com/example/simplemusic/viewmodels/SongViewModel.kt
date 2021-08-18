package com.example.simplemusic.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.simplemusic.models.multimediacontent.AlbumSong
import com.example.simplemusic.models.stored.AppDatabase
import com.example.simplemusic.repositories.SongRepository

/**
 * View model for songs.
 */
class SongViewModel(application: Application) : AndroidViewModel(application) {

    val songs = MutableLiveData<List<AlbumSong>>()
    private val songRepository = SongRepository()

    // UI
    var selectedAlbum: String? = null
    var searchingSongs: Boolean = false

    suspend fun searchAlbumSongs(albumId: Int, limit: Int) {
        searchingSongs = true
        songs.value = songRepository.getAlbumSongs(albumId, limit)
    }
}