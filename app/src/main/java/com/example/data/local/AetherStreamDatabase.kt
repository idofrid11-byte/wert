package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.local.dao.WatchlistDao
import com.example.data.local.entity.WatchlistItemEntity

/**
 * Main Room database for AetherStream, storing local user data such as watchlists,
 * resume points, and favorites.
 */
@Database(
    entities = [WatchlistItemEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AetherStreamDatabase : RoomDatabase() {

    abstract fun watchlistDao(): WatchlistDao

    companion object {
        private const val DATABASE_NAME = "aetherstream.db"

        @Volatile
        private var INSTANCE: AetherStreamDatabase? = null

        fun getInstance(context: Context): AetherStreamDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: buildDatabase(context).also { INSTANCE = it }
            }
        }

        private fun buildDatabase(context: Context): AetherStreamDatabase {
            return Room.databaseBuilder(
                context.applicationContext,
                AetherStreamDatabase::class.java,
                DATABASE_NAME
            )
            .fallbackToDestructiveMigration()
            .build()
        }
    }
}
