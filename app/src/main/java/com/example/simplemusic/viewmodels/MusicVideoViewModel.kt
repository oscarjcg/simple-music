package com.example.simplemusic.viewmodels

import android.os.Parcelable
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

    // List scroll
    var recyclerViewState: Parcelable? = null

    var waitShare = false

    // UI
    var searchingMusicVideos: Boolean = false

    suspend fun searchArtistMusicVideos(term: String) {
        searchingMusicVideos = true
        musicVideos.value = musicVideoRepository.getArtistMusicVideos(term)
    }

    suspend fun deleteAll() {
        musicVideoRepository.deleteAll()
    }

    fun resetPagination() {
        musicVideoRepository.resetPagination()
        recyclerViewState = null
        waitShare = false
    }

    fun canGetMoreData(): Boolean {
        val size = musicVideos.value?.size ?: Int.MAX_VALUE
        return musicVideoRepository.pagination <= size
    }
}