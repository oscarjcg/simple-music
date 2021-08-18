package com.example.simplemusic.models.stored

import androidx.room.ColumnInfo
import androidx.room.Entity

@Entity(primaryKeys = ["userId", "trackId"])
data class UserLikesTrack(
    val userId: Long,
    val trackId: Long
)