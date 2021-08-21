package com.example.simplemusic.models.multimediacontent

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.util.*

/**
 * Music video data structure.
 */
@Entity(tableName = "music_video", indices = [Index(value = ["trackId"], unique = true)])
class MusicVideo (
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

    @SerializedName("artworkUrl100")
    @Expose
    var artworkUrl100: String? = null,

    @SerializedName("releaseDate")
    @Expose
    var releaseDate: Date? = null,

    @SerializedName("_limit")
    var limit: Int? = null,

    @SerializedName("_artistIdOwner")
    var artistOwner: String? = null,

    @SerializedName("_order")
    var order: Int? = null,
): MultimediaContent()