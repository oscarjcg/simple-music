package com.example.simplemusic.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.simplemusic.models.RepositoryResult
import com.example.simplemusic.utils.UIEvent
import com.example.simplemusic.models.multimediacontent.Artist
import com.example.simplemusic.repositories.ArtistRepository
import com.example.simplemusic.utils.Event
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.lang.Exception
import javax.inject.Inject

/**
 * View model for artists.
 */

private const val DEBOUNCE_SEARCH_TIME = 300L

@HiltViewModel
class ArtistViewModel
    @Inject
    constructor(
        private val artistRepository: ArtistRepository
    ) : ViewModel() {
    val artists = MutableLiveData<List<Artist>>()

    var searchedArtist: String? = null
    val loading = MutableLiveData(false)
    val showStateInfo = MutableLiveData(false)
    val stateInfo = MutableLiveData<String>()
    val uiState = MutableLiveData<Event<UIEvent<Nothing>>>()
    var animating = false
    private var searchJob: Job? = null

    fun setLoading(loading: Boolean) {
        this.loading.value = loading
    }

    fun setStateInfo(show: Boolean, message: String = "") {
        showStateInfo.value = show
        stateInfo.value = message
    }

    fun searchArtist(term: String) {
        searchJob?.cancel()
        searchedArtist = term
        setLoading(true)
        setStateInfo(false)

        searchJob = viewModelScope.launch {
            delay(DEBOUNCE_SEARCH_TIME)
            val repositoryResult = artistRepository.getArtists(term)
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

    private fun handleSuccess(data: List<Artist>) {
        artists.value = data
        artists.value?.let {
            if (it.isNotEmpty()) {
                setStateInfo(false)
            }
        }
    }

    private fun handleError(exception: Exception) {
        exception.printStackTrace()

        uiState.value = Event(UIEvent.CheckInternet)
        artists.value?.let {
            if (it.isEmpty()) {
                uiState.value = Event(UIEvent.EmptyList)
            }
        }
    }

    fun resetPagination() {
        artistRepository.resetPagination()
    }

    fun canGetMoreData(): Boolean {
        val size = artists.value?.size ?: Int.MAX_VALUE
        return artistRepository.pagination <= size
    }
}
