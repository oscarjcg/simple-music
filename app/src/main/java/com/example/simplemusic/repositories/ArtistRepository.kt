package com.example.simplemusic.repositories

import com.example.simplemusic.models.Artist
import com.example.simplemusic.models.SearchResponse
import com.example.simplemusic.webservices.SearchWebService
import com.google.gson.GsonBuilder
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ArtistRepository {
    private val BASE_URL = "https://itunes.apple.com/"

    suspend fun getArtists(term: String, limit: Int): List<Artist>? {
        val searchResponse = getRetrofit().create(SearchWebService::class.java).getArtists(term, limit)
        return searchResponse.results
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