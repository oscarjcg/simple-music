package com.example.simplemusic.models.stored.search

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.util.*

@Entity(tableName = "search")
data class Search(
    val term: String,
    var limit: Int,
    @SerializedName("_cacheDate")
    @Expose
    var cacheDate: Date? = null,
    @PrimaryKey(autoGenerate = true)
    val searchId: Long = 0,
)
