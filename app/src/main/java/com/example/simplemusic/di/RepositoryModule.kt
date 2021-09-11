package com.example.simplemusic.di

import com.example.simplemusic.database.dao.ApiCacheDao
import com.example.simplemusic.database.dao.SearchDao
import com.example.simplemusic.database.dao.UserDao
import com.example.simplemusic.repositories.*
import com.example.simplemusic.webservices.SearchWebService
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
        searchWebService: SearchWebService
    ): AlbumRepository {
        return AlbumRepository(apiCacheDao, searchWebService)
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
        searchWebService: SearchWebService
    ): MusicVideoRepository {
        return MusicVideoRepository(apiCacheDao, searchWebService)
    }

    @Singleton
    @Provides
    fun provideSongRepository(
        apiCacheDao: ApiCacheDao,
        searchWebService: SearchWebService
    ): SongRepository {
        return SongRepository(apiCacheDao, searchWebService)
    }

    @Singleton
    @Provides
    fun provideUserRepository(
        userDao: UserDao
    ): UserRepository {
        return UserRepository(userDao)
    }

}