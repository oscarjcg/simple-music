package com.example.simplemusic.repositories

import com.example.simplemusic.models.multimediacontent.AlbumSong
import com.example.simplemusic.models.multimediacontent.Artist
import com.example.simplemusic.utils.BASE_URL
import com.example.simplemusic.webservices.SearchWebService
import com.google.gson.GsonBuilder
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class SongRepository {

    suspend fun getAlbumSongs(albumId: Int, limit: Int): List<AlbumSong> {
        val searchResponse = getRetrofit().create(SearchWebService::class.java).getAlbumSongs(albumId, limit)

        // Filter. Only songs
        return searchResponse.results?.filterIsInstance<AlbumSong>() as ArrayList<AlbumSong>
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