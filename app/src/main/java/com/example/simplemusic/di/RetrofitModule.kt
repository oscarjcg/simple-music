package com.example.simplemusic.di

import com.example.simplemusic.utils.BASE_URL
import com.example.simplemusic.webservices.AlbumWebService
import com.example.simplemusic.webservices.MusicVideoWebService
import com.example.simplemusic.webservices.SearchWebService
import com.example.simplemusic.webservices.SongWebService
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

/**
 * Provides network service.
 */
@Module
@InstallIn(SingletonComponent::class)
object RetrofitModule {

    @Singleton
    @Provides
    fun provideGsonBuilder(): Gson {
        return GsonBuilder().create()
    }

    @Singleton
    @Provides
    fun provideRetrofit(gson: Gson): Retrofit.Builder {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
    }

    @Singleton
    @Provides
    fun provideSearchWebService(retrofit: Retrofit.Builder): SearchWebService {
        return retrofit
            .build()
            .create(SearchWebService::class.java)
    }

    @Singleton
    @Provides
    fun provideAlbumWebService(retrofit: Retrofit.Builder): AlbumWebService {
        return retrofit
            .build()
            .create(AlbumWebService::class.java)
    }

    @Singleton
    @Provides
    fun provideMusicVideoWebService(retrofit: Retrofit.Builder): MusicVideoWebService {
        return retrofit
            .build()
            .create(MusicVideoWebService::class.java)
    }

    @Singleton
    @Provides
    fun provideSongWebService(retrofit: Retrofit.Builder): SongWebService {
        return retrofit
            .build()
            .create(SongWebService::class.java)
    }
}