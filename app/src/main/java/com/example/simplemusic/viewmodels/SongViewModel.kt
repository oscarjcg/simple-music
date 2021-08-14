package com.example.simplemusic.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.simplemusic.models.multimediacontent.AlbumSong
import com.example.simplemusic.repositories.SongRepository

class SongViewModel : ViewModel() {

    val songs = MutableLiveData<List<AlbumSong>>()
    private val songRepository = SongRepository()

    suspend fun searchAlbumSongs(albumId: Int, limit: Int) {
        songs.value = songRepository.getAlbumSongs(albumId, limit)
    }
}