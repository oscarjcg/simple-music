package com.example.simplemusic.viewmodels

import android.os.Parcelable
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.simplemusic.models.multimediacontent.ArtistAlbum
import com.example.simplemusic.repositories.AlbumRepository
import dagger.hilt.android.lifecycle.HiltViewModel
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
    var searchingAlbums: Boolean = false

    suspend fun searchArtistAlbum(artistId: Int) {
        searchingAlbums = true
        albums.value = albumRepository.getArtistAlbums(artistId)
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