package com.example.simplemusic.repositories

import com.example.simplemusic.models.stored.User
import com.example.simplemusic.database.dao.UserDao
import com.example.simplemusic.models.stored.UserLikesTrack

class UserRepository(private val userDao: UserDao) {

    suspend fun getUsers(): List<User> {
        return userDao.getAll()
    }

    suspend fun addUser(user: User) {
        userDao.addUser(user)
    }

    suspend fun findUserByName(name: String): User {
        return userDao.findUserByName(name)
    }

    suspend fun addUserLikesTrack(userId: Long, trackId: Long) {
        userDao.addUserLikesTrack(UserLikesTrack(userId, trackId))
    }

    suspend fun deleteUserLikesTrack(userId: Long, trackId: Long) {
        userDao.deleteUserLikesTrack(UserLikesTrack(userId, trackId))
    }

    suspend fun getUserLikesTrack(userId: Long): List<Long> {
        val liked = userDao.getUserLikesTrack(userId).get(0)
        val tracksId = ArrayList<Long>()
        for (like in liked.likedTracks)
            tracksId.add(like.trackId)
        return tracksId
    }
}