package com.example.simplemusic.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.example.simplemusic.database.AppDatabase
import com.example.simplemusic.models.stored.DEFAULT_USER_NAME
import com.example.simplemusic.models.stored.User
import com.example.simplemusic.repositories.UserRepository

/**
 * View model for users. Current only a default user.
 */
class UserViewModel(application: Application) : AndroidViewModel(application) {
    val user = MutableLiveData<User>()

    private var userRepository : UserRepository

    init {
        val db = AppDatabase.getDatabase(application)
        userRepository = UserRepository(db.userDao())
    }

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