package com.example.simplemusic.models.multimediacontent

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.simplemusic.models.multimediacontent.MultimediaContent
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.util.*

/**
 * Album data structure.
 */
@Entity(tableName = "album", indices = [Index(value = ["collectionId"], unique = true)])
data class ArtistAlbum(
    @SerializedName("collectionName")
    @Expose
    var collectionName: String? = null,

    @PrimaryKey
    @SerializedName("collectionId")
    @Expose
    var collectionId: Long? = null,

    @SerializedName("artworkUrl100")
    @Expose
    var artworkUrl100: String? = null,

    @SerializedName("releaseDate")
    @Expose
    var releaseDate: Date? = null,

    @SerializedName("trackCount")
    @Expose
    var trackCount: Int? = null,

    @SerializedName("artistId")
    @Expose
    var artistId: Int? = null,

    @SerializedName("_limit")
    var limit: Int? = null,

    @SerializedName("_artistIdOwner")
    var artistIdOwner: Int? = null,

    @SerializedName("_order")
    var order: Int? = null,
    ) : MultimediaContent()
