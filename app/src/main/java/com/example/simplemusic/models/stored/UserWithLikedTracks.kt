package com.example.simplemusic.models.stored

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation

data class UserWithLikedTracks(
    @Embedded
    var user: User,
    @Relation(
        parentColumn = "userId",
        entityColumn = "trackId",
        associateBy = Junction(UserLikesTrack::class)
    )
    var likedTracks: List<UserLikesTrack>
)
