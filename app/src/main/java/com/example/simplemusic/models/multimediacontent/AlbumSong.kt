package com.example.simplemusic.models.multimediacontent

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.util.*

/**
 * Song data structure.
 */
@Entity(tableName = "song", indices = [Index(value = ["trackId"], unique = true)])
data class AlbumSong(
    @PrimaryKey
    @SerializedName("trackId")
    @Expose
    var trackId: Long? = null,

    @SerializedName("trackName")
    @Expose
    var trackName: String? = null,

    @SerializedName("previewUrl")
    @Expose
    var previewUrl: String? = null,

    // Helper
    @SerializedName("_like")
    var like: Boolean? = null,

    @SerializedName("_limit")
    var limit: Int? = null,

    @SerializedName("_collectionIdOwner")
    var collectionIdOwner: Long? = null,

    @SerializedName("_order")
    var order: Int? = null,

    @SerializedName("_cacheDate")
    @Expose
    var cacheDate: Date? = null,

    ): MultimediaContent()
