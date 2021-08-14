package com.example.simplemusic.utils.json

import com.example.simplemusic.models.multimediacontent.Artist
import com.example.simplemusic.models.multimediacontent.ArtistAlbum
import com.example.simplemusic.models.multimediacontent.MultimediaContent
import com.google.gson.*
import java.lang.reflect.Type

private const val WRAPPER_TYPE = "wrapperType"
private const val WRAPPER_TYPE_UNKNOWN= "unknown"
private const val WRAPPER_TYPE_ARTIST = "artist"
private const val WRAPPER_TYPE_COLLECTION = "collection"

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

            return when (wrapperType) {
                WRAPPER_TYPE_ARTIST -> Gson().fromJson(json, Artist::class.java)
                WRAPPER_TYPE_COLLECTION -> Gson().fromJson(json, ArtistAlbum::class.java)
                else -> multimediaContent
            }
        }

        return multimediaContent;
    }

    override fun serialize(
        src: MultimediaContent?,
        typeOfSrc: Type?,
        context: JsonSerializationContext?
    ): JsonElement {
        TODO("Not yet implemented")
    }

}