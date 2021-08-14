package com.example.simplemusic.repositories

import com.example.simplemusic.models.multimediacontent.Artist
import com.example.simplemusic.utils.BASE_URL
import com.example.simplemusic.webservices.SearchWebService
import com.google.gson.GsonBuilder
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ArtistRepository {

    suspend fun getArtists(term: String, limit: Int): List<Artist> {
        val searchResponse = getRetrofit().create(SearchWebService::class.java).getArtists(term, limit)

        // Filter. Only artist
        return searchResponse.results?.filterIsInstance<Artist>() as ArrayList<Artist>
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