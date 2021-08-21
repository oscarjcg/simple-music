package com.example.simplemusic.models.stored.search

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "search")
data class Search(
    val term: String,
    var limit: Int,
    @PrimaryKey(autoGenerate = true)
    val searchId: Long = 0,
)