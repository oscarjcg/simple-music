package com.example.simplemusic.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.simplemusic.models.multimediacontent.MusicVideo
import com.example.simplemusic.repositories.MusicVideoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * View model for music videos.
 */
@HiltViewModel
class MusicVideoViewModel
    @Inject
    constructor(
        private val musicVideoRepository: MusicVideoRepository
    ) : ViewModel()  {

    var musicVideos = MutableLiveData<List<MusicVideo>>()

    // UI
    var searchingMusicVideos: Boolean = false

    suspend fun searchArtistMusicVideos(term: String, limit: Int) {
        searchingMusicVideos = true
        musicVideos.value = musicVideoRepository.getArtistMusicVideos(term, limit)
    }

    suspend fun deleteAll() {
        musicVideoRepository.deleteAll()
    }
}