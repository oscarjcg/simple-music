package com.example.simplemusic.di

import com.example.simplemusic.database.dao.ApiCacheDao
import com.example.simplemusic.database.dao.SearchDao
import com.example.simplemusic.database.dao.UserDao
import com.example.simplemusic.repositories.*
import com.example.simplemusic.webservices.AlbumWebService
import com.example.simplemusic.webservices.MusicVideoWebService
import com.example.simplemusic.webservices.SearchWebService
import com.example.simplemusic.webservices.SongWebService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Provides repositories.
 */
@InstallIn(SingletonComponent::class)
@Module
object RepositoryModule {

    @Singleton
    @Provides
    fun provideAlbumRepository(
        apiCacheDao: ApiCacheDao,
        albumWebService: AlbumWebService
    ): AlbumRepository {
        return AlbumRepository(apiCacheDao, albumWebService)
    }

    @Singleton
    @Provides
    fun provideArtistRepository(
        apiCacheDao: ApiCacheDao,
        searchDao: SearchDao,
        searchWebService: SearchWebService
    ): ArtistRepository {
        return ArtistRepository(apiCacheDao, searchDao, searchWebService)
    }

    @Singleton
    @Provides
    fun provideMusicVideoRepository(
        apiCacheDao: ApiCacheDao,
        musicVideoWebService: MusicVideoWebService
    ): MusicVideoRepository {
        return MusicVideoRepository(apiCacheDao, musicVideoWebService)
    }

    @Singleton
    @Provides
    fun provideSongRepository(
        apiCacheDao: ApiCacheDao,
        songWebService: SongWebService
    ): SongRepository {
        return SongRepository(apiCacheDao, songWebService)
    }

    @Singleton
    @Provides
    fun provideUserRepository(
        userDao: UserDao
    ): UserRepository {
        return UserRepository(userDao)
    }

}