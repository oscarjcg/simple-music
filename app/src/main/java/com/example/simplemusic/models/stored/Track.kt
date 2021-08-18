package com.example.simplemusic.models.stored

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "track")
data class Track (
    @PrimaryKey
    val trackId: Long,
    var trackName: String
)
