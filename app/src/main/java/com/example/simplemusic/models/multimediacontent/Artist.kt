package com.example.simplemusic.models.multimediacontent

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.simplemusic.models.multimediacontent.MultimediaContent
import com.google.gson.annotations.SerializedName
import com.google.gson.annotations.Expose

/**
 * Artist data structure.
 */
@Entity(tableName = "artist", indices = [Index(value = ["artistId"], unique = true)])
data class Artist(

    @PrimaryKey
    @SerializedName("artistId")
    @Expose
    var artistId: Int? = null,

    @SerializedName("artistType")
    @Expose
    var artistType: String? = null,

    @SerializedName("artistName")
    @Expose
    var artistName: String? = null,

    @SerializedName("artistLinkUrl")
    @Expose
    var artistLinkUrl: String? = null,

    @SerializedName("primaryGenreName")
    @Expose
    var primaryGenreName: String? = null,

    @SerializedName("primaryGenreId")
    @Expose
    var primaryGenreId: Int? = null
): MultimediaContent()