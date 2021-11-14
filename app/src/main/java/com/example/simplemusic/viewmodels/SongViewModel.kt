package com.example.simplemusic.viewmodels

import android.os.Parcelable
import androidx.lifecycle.*
import com.example.simplemusic.models.multimediacontent.AlbumSong
import com.example.simplemusic.utils.UIEvent
import com.example.simplemusic.models.RepositoryResult
import com.example.simplemusic.repositories.SongRepository
import com.example.simplemusic.utils.Event
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.lang.Exception
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

    // List scroll
    var recyclerViewState: Parcelable? = null

    var waitShare = false

    val loading = MutableLiveData(false)
    val playingSong = MutableLiveData(false)
    val playingSongName = MutableLiveData<String>()
    val showStateInfo = MutableLiveData(false)
    val stateInfo = MutableLiveData<String>()
    val uiState = MutableLiveData<Event<UIEvent<Nothing>>>()

    fun setLoading(loading: Boolean) {
        this.loading.value = loading
    }

    fun setPlayingSong(playing: Boolean, name: String = "") {
        playingSong.value = playing
        playingSongName.value = name
    }

    fun setStateInfo(show: Boolean, message: String = "") {
        showStateInfo.value = show
        stateInfo.value = message
    }

    fun searchAlbumSongs(albumId: Long) {
        setLoading(true)
        setStateInfo(false)

        viewModelScope.launch {
            val repositoryResult = songRepository.getAlbumSongs(albumId)
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

    private fun handleSuccess(data: List<AlbumSong>) {
        songs.value = data
        songs.value?.let {
            if (it.isNotEmpty()) {
                setStateInfo(false)
            }
        }
    }

    private fun handleError(exception: Exception) {
        exception.printStackTrace()

        uiState.value = Event(UIEvent.CheckInternet)
        songs.value?.let {
            if (it.isEmpty()) {
                uiState.value = Event(UIEvent.EmptyList)
            }
        }
    }

    fun resetPagination() {
        songRepository.resetPagination()
        recyclerViewState = null
        waitShare = false
    }

    fun canGetMoreData(): Boolean {
        val size = songs.value?.size ?: Int.MAX_VALUE
        return songRepository.pagination <= size
    }
}
