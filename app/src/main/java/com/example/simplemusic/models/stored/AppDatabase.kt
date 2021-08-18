package com.example.simplemusic.models.stored

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [User::class, Track::class, UserLikesTrack::class], version = 1)
abstract class AppDatabase: RoomDatabase() {
    abstract fun userDao(): UserDao

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
