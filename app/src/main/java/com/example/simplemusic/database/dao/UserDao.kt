package com.example.simplemusic.database.dao

import androidx.room.*
import com.example.simplemusic.models.stored.User
import com.example.simplemusic.models.stored.UserLikesTrack
import com.example.simplemusic.models.stored.UserWithLikedTracks

@Dao
interface UserDao {
    @Insert
    suspend fun addUser(user: User)

    @Query("SELECT * FROM user")
    suspend fun getAll(): List<User>

    @Query("SELECT * FROM user WHERE name = :name LIMIT 1")
    suspend fun findUserByName(name: String): User

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addUserLikesTrack(join: UserLikesTrack)

    @Transaction
    @Query("SELECT * FROM user WHERE userId = :userId")
    suspend fun getUserLikesTrack(userId: Long): List<UserWithLikedTracks>

    @Delete
    suspend fun deleteUserLikesTrack(userLikesTrack: UserLikesTrack)
}