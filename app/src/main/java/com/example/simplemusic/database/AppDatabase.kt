package com.example.simplemusic.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.simplemusic.models.multimediacontent.AlbumSong
import com.example.simplemusic.models.multimediacontent.Artist
import com.example.simplemusic.models.multimediacontent.ArtistAlbum
import com.example.simplemusic.models.multimediacontent.MusicVideo
import com.example.simplemusic.models.stored.User
import com.example.simplemusic.database.dao.UserDao
import com.example.simplemusic.models.stored.UserLikesTrack
import com.example.simplemusic.database.dao.ApiCacheDao
import com.example.simplemusic.models.stored.search.Search
import com.example.simplemusic.models.stored.search.SearchResultArtist

/**
 * Singleton room database.
 */
@Database(entities = [
    User::class,
    AlbumSong::class,
    UserLikesTrack::class,
    ArtistAlbum::class,
    MusicVideo::class,
    Artist::class,
    Search::class,
    SearchResultArtist::class], version = 1)
@TypeConverters(Converters::class)
abstract class AppDatabase: RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun apiCacheDao(): ApiCacheDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            val aux = INSTANCE
            if (aux != null) {
                return aux
            }

            synchronized(this) {
                val instance = Room.databaseBuilder(
                                    context.applicationContext,
                                    AppDatabase::class.java,
                                    "room_database"
                                ).build()
                INSTANCE = instance
                return instance
            }
        }
    }
}
