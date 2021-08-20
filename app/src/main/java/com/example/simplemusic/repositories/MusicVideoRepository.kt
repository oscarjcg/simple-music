package com.example.simplemusic.repositories

import com.example.simplemusic.models.multimediacontent.Artist
import com.example.simplemusic.models.multimediacontent.MusicVideo
import com.example.simplemusic.utils.BASE_URL
import com.example.simplemusic.webservices.SearchWebService
import com.google.gson.GsonBuilder
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MusicVideoRepository {

    suspend fun getArtistMusicVideos(term: String, limit: Int): List<MusicVideo> {
        val searchResponse = getRetrofit().create(SearchWebService::class.java).getArtistMusicVideos(term, limit)

        // Filter. Only artist
        return searchResponse.results?.filterIsInstance<MusicVideo>() as ArrayList<MusicVideo>
    }

    private fun getRetrofit(): Retrofit {
        val gson = GsonBuilder()
            .create()

        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }
}