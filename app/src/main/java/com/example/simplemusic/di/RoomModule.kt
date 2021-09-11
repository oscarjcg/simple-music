package com.example.simplemusic.di

import android.content.Context
import androidx.room.Room
import com.example.simplemusic.database.AppDatabase
import com.example.simplemusic.database.dao.ApiCacheDao
import com.example.simplemusic.database.dao.SearchDao
import com.example.simplemusic.database.dao.UserDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Provides database.
 */
@InstallIn(SingletonComponent::class)
@Module
object RoomModule {

    @Singleton
    @Provides
    fun provideRoomDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context.applicationContext,
            AppDatabase::class.java,
            "room_database"
        ).build()
    }

    @Singleton
    @Provides
    fun provideUserDao(database: AppDatabase): UserDao {
        return database.userDao()
    }

    @Singleton
    @Provides
    fun provideApiCacheDao(database: AppDatabase): ApiCacheDao {
        return database.apiCacheDao()
    }

    @Singleton
    @Provides
    fun provideSearchDao(database: AppDatabase): SearchDao {
        return database.searchDao()
    }
}