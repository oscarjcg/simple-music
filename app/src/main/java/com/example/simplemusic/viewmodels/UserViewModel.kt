package com.example.simplemusic.viewmodels

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.example.simplemusic.models.stored.AppDatabase
import com.example.simplemusic.models.stored.DEFAULT_USER_NAME
import com.example.simplemusic.models.stored.User
import com.example.simplemusic.repositories.UserRepository

class UserViewModel(application: Application) : AndroidViewModel(application) {
    val user = MutableLiveData<User>()

    private var userRepository : UserRepository

    init {
        val db = AppDatabase.getDatabase(application)
        userRepository = UserRepository(db.userDao())
    }

    suspend fun setDefaultUser() {
        val users = userRepository.getUsers()
        if (users.isEmpty())
            userRepository.addUser(User(DEFAULT_USER_NAME))

        user.value = userRepository.findUserByName(DEFAULT_USER_NAME)

        Log.println(Log.ERROR, "DEBUG", "request ${users.size}")//
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