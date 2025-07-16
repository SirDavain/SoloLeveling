package com.example.sololevelingapplication

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import android.content.Context

@Database(entities = [QuestEntity::class, UserStatsEntity::class], version = 1)
abstract class AppDatabase: RoomDatabase() {
    abstract fun questDao(): QuestDao
    abstract fun userStatsDao(): UserStatsDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "solo_leveling_database" // Your database name
                )
                    // .addMigrations(...) // Add migrations if you change schema later
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}