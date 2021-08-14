package com.example.simplemusic.repositories

import com.example.simplemusic.models.multimediacontent.ArtistAlbum
import com.example.simplemusic.utils.BASE_URL
import com.example.simplemusic.webservices.SearchWebService
import com.google.gson.GsonBuilder
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class AlbumRepository {

    suspend fun getArtistAlbums(artistId: Int, limit: Int): List<ArtistAlbum> {
        val searchResponse = getRetrofit().create(SearchWebService::class.java).getArtistAlbums(artistId, limit)

        // Filter. Only albums
        return searchResponse.results?.filterIsInstance<ArtistAlbum>() as ArrayList<ArtistAlbum>
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