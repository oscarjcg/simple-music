package com.example.simplemusic.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.simplemusic.models.multimediacontent.AlbumSong
import com.example.simplemusic.models.stored.DEFAULT_USER_NAME
import com.example.simplemusic.models.stored.User
import com.example.simplemusic.repositories.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * View model for users. Currently only a default user.
 */
@HiltViewModel
class UserViewModel

    @Inject
    constructor(
        private var userRepository : UserRepository
    ) : ViewModel() {

    val user = MutableLiveData<User>()
    val likedTracksId = MutableLiveData<List<Long>>()

    /**
     * Add a default user if none created. Or finds a default user.
     */
    fun setDefaultUser() {
        viewModelScope.launch {
            val users = userRepository.getUsers()
            if (users.isEmpty())
                userRepository.addUser(User(DEFAULT_USER_NAME))

            user.value = userRepository.findUserByName(DEFAULT_USER_NAME)
        }
    }

    fun userLikesTrack(song: AlbumSong) {
        viewModelScope.launch {
            user.value?.let {
                // Switch like . Set or remove like
                if (song.like!!)
                    deleteUserLikesTrack(it.userId, song.trackId!!)
                else
                    addUserLikesTrack(it.userId, song.trackId!!)
                song.like = !song.like!!

                getUserLikesTrack(user.value!!.userId)
            }
        }
    }

    fun getUserLikesTrack(userId: Long) {
        viewModelScope.launch {
            likedTracksId.value = userRepository.getUserLikesTrack(userId)
        }
    }

    private suspend fun addUserLikesTrack(userId: Long, trackId: Long) {
        userRepository.addUserLikesTrack(userId, trackId)
    }

    private suspend fun deleteUserLikesTrack(userId: Long, trackId: Long) {
        userRepository.deleteUserLikesTrack(userId, trackId)
    }
}
