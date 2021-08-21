package com.example.simplemusic.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.example.simplemusic.models.multimediacontent.MusicVideo
import com.example.simplemusic.database.AppDatabase
import com.example.simplemusic.repositories.MusicVideoRepository

/**
 * View model for music videos.
 */
class MusicVideoViewModel(application: Application) : AndroidViewModel(application)  {

    var musicVideos = MutableLiveData<List<MusicVideo>>()
    private val musicVideoRepository: MusicVideoRepository

    init {
        val db = AppDatabase.getDatabase(application)
        musicVideoRepository = MusicVideoRepository(db.apiCacheDao())
    }

    // UI
    var searchingMusicVideos: Boolean = false

    suspend fun searchArtistMusicVideos(term: String, limit: Int) {
        searchingMusicVideos = true
        musicVideos.value = musicVideoRepository.getArtistMusicVideos(term, limit)
    }
}