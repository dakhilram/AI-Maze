package com.akhil.aimaze.data.history

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [BenchmarkHistoryEntity::class],
    version = 1,
    exportSchema = false,
)
abstract class AiMazeDatabase : RoomDatabase() {
    abstract fun benchmarkHistoryDao(): BenchmarkHistoryDao

    companion object {
        @Volatile private var instance: AiMazeDatabase? = null

        fun get(context: Context): AiMazeDatabase = instance ?: synchronized(this) {
            instance ?: Room.databaseBuilder(
                context.applicationContext,
                AiMazeDatabase::class.java,
                "ai-maze.db",
            ).build().also { instance = it }
        }
    }
}
