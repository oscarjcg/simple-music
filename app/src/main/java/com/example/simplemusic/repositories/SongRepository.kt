package com.example.simplemusic.repositories

import android.util.Log
import androidx.lifecycle.LiveData
import com.example.simplemusic.models.multimediacontent.AlbumSong
import com.example.simplemusic.models.multimediacontent.Artist
import com.example.simplemusic.models.stored.User
import com.example.simplemusic.models.stored.UserDao
import com.example.simplemusic.utils.BASE_URL
import com.example.simplemusic.webservices.SearchWebService
import com.google.gson.GsonBuilder
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class SongRepository(private val userDao: UserDao) {

    suspend fun getAlbumSongs(albumId: Int, limit: Int): List<AlbumSong> {
        val searchResponse = getRetrofit().create(SearchWebService::class.java).getAlbumSongs(albumId, limit)

        // Filter. Only songs
        return searchResponse.results?.filterIsInstance<AlbumSong>() as ArrayList<AlbumSong>
    }

    suspend fun getLikedSongs(): List<User> {

        var u = userDao.getAll()
        Log.println(Log.ERROR, "DEBUG", "request ${u.size}")//
        return u
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