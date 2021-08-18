package com.example.simplemusic.models.stored

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

const val DEFAULT_USER_NAME = "default_user"

@Entity(tableName = "user")
data class User(
    val name: String,
    @PrimaryKey(autoGenerate = true)
    val userId: Long = 0,
)
