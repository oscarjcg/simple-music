package com.example.simplemusic.models.stored.search

import androidx.room.Entity

@Entity(tableName = "search_result_artist", primaryKeys = ["searchId", "artistId"])
data class SearchResultArtist(
    val searchId: Long,
    val artistId: Int,
    val order: Int
)