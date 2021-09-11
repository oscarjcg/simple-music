package com.example.simplemusic.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.simplemusic.models.stored.DEFAULT_USER_NAME
import com.example.simplemusic.models.stored.User
import com.example.simplemusic.repositories.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
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

    /**
     * Add a default user if none created. Or finds a default user.
     */
    suspend fun setDefaultUser() {
        val users = userRepository.getUsers()
        if (users.isEmpty())
            userRepository.addUser(User(DEFAULT_USER_NAME))

        user.value = userRepository.findUserByName(DEFAULT_USER_NAME)
    }

    suspend fun addUserLikesTrack(userId: Long, trackId: Long) {
        userRepository.addUserLikesTrack(userId, trackId)
    }

    suspend fun getUserLikesTrack(userId: Long): List<Long> {
        return userRepository.getUserLikesTrack(userId)
    }

    suspend fun deleteUserLikesTrack(userId: Long, trackId: Long) {
        userRepository.deleteUserLikesTrack(userId, trackId)
    }
}