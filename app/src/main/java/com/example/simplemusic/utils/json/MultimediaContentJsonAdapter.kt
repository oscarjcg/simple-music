package com.example.simplemusic.utils.json

import com.example.simplemusic.models.multimediacontent.*
import com.google.gson.*
import java.lang.reflect.Type

private const val WRAPPER_TYPE = "wrapperType"
private const val WRAPPER_TYPE_UNKNOWN= "unknown"
private const val WRAPPER_TYPE_ARTIST = "artist"
private const val WRAPPER_TYPE_COLLECTION = "collection"
private const val WRAPPER_TYPE_TRACK = "track"

private const val TRACK_KIND = "kind"
private const val TRACK_KIND_UNKNOWN= "track_kind_unknown"
private const val TRACK_KIND_SONG = "song"
private const val TRACK_KIND_MUSIC_VIDEO = "music-video"

/**
 * Gson adapter for the search API. Return an array of artists, albums, song and unknown.
 * All of them are inheriting from MultimediaContent, the top wrapper.
 */
class MultimediaContentJsonAdapter : JsonDeserializer<MultimediaContent>,
    JsonSerializer<MultimediaContent> {
    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): MultimediaContent {
        val jsonObject = json?.asJsonObject

        // If unknown type
        val multimediaContent = MultimediaContent()
        multimediaContent.wrapperType = WRAPPER_TYPE_UNKNOWN

        if (jsonObject != null) {
            val wrapperType = jsonObject.get(WRAPPER_TYPE).asString

            // Parsing respective type
            return when (wrapperType) {
                WRAPPER_TYPE_ARTIST -> Gson().fromJson(json, Artist::class.java)
                WRAPPER_TYPE_COLLECTION -> Gson().fromJson(json, ArtistAlbum::class.java)
                WRAPPER_TYPE_TRACK -> typeWrapperTrack(json)
                else -> multimediaContent
            }
        }

        return multimediaContent
    }

    private fun typeWrapperTrack(json: JsonElement): MultimediaContent {
        val jsonObject = json.asJsonObject

        // If unknown type
        val multimediaContent = MultimediaContent()
        multimediaContent.wrapperType = WRAPPER_TYPE_UNKNOWN

        val kind = jsonObject.get(TRACK_KIND).asString
        return when (kind) {
            TRACK_KIND_SONG -> Gson().fromJson(json, AlbumSong::class.java)
            TRACK_KIND_MUSIC_VIDEO -> Gson().fromJson(json, MusicVideo::class.java)
            else -> multimediaContent
        }
    }

    override fun serialize(
        src: MultimediaContent?,
        typeOfSrc: Type?,
        context: JsonSerializationContext?
    ): JsonElement {
        TODO("Not yet implemented")
    }

}