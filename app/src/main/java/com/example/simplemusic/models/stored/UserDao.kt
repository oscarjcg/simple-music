package com.example.simplemusic.models.stored

import androidx.lifecycle.LiveData
import androidx.room.*

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