package com.example.simplemusic.viewmodels

import android.os.Parcelable
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.simplemusic.models.RepositoryResult
import com.example.simplemusic.models.UIEvent
import com.example.simplemusic.models.multimediacontent.AlbumSong
import com.example.simplemusic.models.multimediacontent.MusicVideo
import com.example.simplemusic.repositories.MusicVideoRepository
import com.example.simplemusic.utils.Event
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.lang.Exception
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

    val loading = MutableLiveData(false)
    val showStateInfo = MutableLiveData(false)
    val showVideoPlayer = MutableLiveData(false)
    val stateInfo = MutableLiveData<String>()
    val uiState = MutableLiveData<Event<UIEvent<Nothing>>>()

    fun setLoading(loading: Boolean) {
        this.loading.value = loading
    }

    fun setStateInfo(show: Boolean, message: String = "") {
        showStateInfo.value = show
        stateInfo.value = message
    }

    fun setShowVideoPlayer(show: Boolean) {
        setLoading(show)
        showVideoPlayer.value = show
    }

    fun searchArtistMusicVideos(term: String) {
        setLoading(true)
        setStateInfo(false)

        viewModelScope.launch {
            val repositoryResult = musicVideoRepository.getArtistMusicVideos(term)
            setLoading(false)

            when(repositoryResult) {
                is RepositoryResult.Success -> {
                    handleSuccess(repositoryResult.data)
                }
                is RepositoryResult.Error -> {
                    handleError(repositoryResult.exception)
                }
            }
        }
    }

    private fun handleSuccess(data: List<MusicVideo>) {
        musicVideos.value = data
        musicVideos.value?.let {
            if (it.isNotEmpty()) {
                setStateInfo(false)
            }
        }
    }

    private fun handleError(exception: Exception) {
        exception.printStackTrace()

        uiState.value = Event(UIEvent.CheckInternet)
        musicVideos.value?.let {
            if (it.isEmpty()) {
                uiState.value = Event(UIEvent.EmptyList)
            }
        }
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
