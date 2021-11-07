package com.example.simplemusic.viewmodels

import android.os.Parcelable
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.simplemusic.models.RepositoryResult
import com.example.simplemusic.models.UIEvent
import com.example.simplemusic.models.multimediacontent.AlbumSong
import com.example.simplemusic.models.multimediacontent.ArtistAlbum
import com.example.simplemusic.repositories.AlbumRepository
import com.example.simplemusic.utils.Event
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.lang.Exception
import javax.inject.Inject

/**
 * View model for albums.
 */
@HiltViewModel
class AlbumViewModel
    @Inject
    constructor(
        private val albumRepository: AlbumRepository
) : ViewModel() {

    val albums = MutableLiveData<List<ArtistAlbum>>()
    // List scroll
    var recyclerViewState: Parcelable? = null

    var waitShare = false

    // UI
    var searchedArtist: String? = null
    val loading = MutableLiveData(false)
    val showStateInfo = MutableLiveData(false)
    val stateInfo = MutableLiveData<String>()
    val uiState = MutableLiveData<Event<UIEvent<Nothing>>>()

    fun setLoading(loading: Boolean) {
        this.loading.value = loading
    }

    fun setStateInfo(show: Boolean, message: String = "") {
        showStateInfo.value = show
        stateInfo.value = message
    }

    fun searchArtistAlbum(artistId: Int) {
        setLoading(true)
        setStateInfo(false)

        viewModelScope.launch {
            val repositoryResult = albumRepository.getArtistAlbums(artistId)
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


    private fun handleSuccess(data: List<ArtistAlbum>) {
        albums.value = data
        albums.value?.let {
            if (it.isNotEmpty()) {
                setStateInfo(false)
            }
        }
    }

    private fun handleError(exception: Exception) {
        exception.printStackTrace()

        uiState.value = Event(UIEvent.CheckInternet)
        albums.value?.let {
            if (it.isEmpty()) {
                uiState.value = Event(UIEvent.EmptyList)
            }
        }
    }
    suspend fun deleteAll() {
        albumRepository.deleteAll()
    }

    fun resetPagination() {
        albumRepository.resetPagination()
        recyclerViewState = null
        waitShare = false
    }

    fun canGetMoreData(): Boolean {
        val size = albums.value?.size ?: Int.MAX_VALUE
        return albumRepository.pagination <= size
    }

}
