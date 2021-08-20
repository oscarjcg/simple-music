package com.example.simplemusic.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.simplemusic.models.multimediacontent.MusicVideo
import com.example.simplemusic.repositories.MusicVideoRepository

/**
 * View model for music videos.
 */
class MusicVideoViewModel : ViewModel()  {

    var musicVideos = MutableLiveData<List<MusicVideo>>()
    private val musicVideoRepository = MusicVideoRepository()

    // UI
    var searchingMusicVideos: Boolean = false

    suspend fun searchArtistMusicVideos(term: String, limit: Int) {
        searchingMusicVideos = true
        musicVideos.value = musicVideoRepository.getArtistMusicVideos(term, limit)
    }
}