package com.example.simplemusic.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.example.simplemusic.models.multimediacontent.AlbumSong
import com.example.simplemusic.database.AppDatabase
import com.example.simplemusic.repositories.SongRepository

/**
 * View model for songs.
 */
class SongViewModel(application: Application) : AndroidViewModel(application) {

    val songs = MutableLiveData<List<AlbumSong>>()
    private var songRepository: SongRepository

    init {
        val db = AppDatabase.getDatabase(application)
        songRepository = SongRepository(db.apiCacheDao())
    }

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