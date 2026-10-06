package com.akhil.aimaze.data.history

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [
        BenchmarkHistoryEntity::class,
        RaceRecordEntity::class,
    ],
    version = 2,
    exportSchema = false,
)
abstract class AiMazeDatabase : RoomDatabase() {
    abstract fun benchmarkHistoryDao(): BenchmarkHistoryDao
    abstract fun raceRecordDao(): RaceRecordDao

    companion object {
        @Volatile private var instance: AiMazeDatabase? = null

        private val migration1To2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS race_records (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        createdAt INTEGER NOT NULL,
                        rows INTEGER NOT NULL,
                        columns INTEGER NOT NULL,
                        mazeSeed INTEGER NOT NULL,
                        winner TEXT NOT NULL,
                        playerMoves INTEGER NOT NULL,
                        botSteps INTEGER NOT NULL
                    )
                    """.trimIndent(),
                )
            }
        }

        fun get(context: Context): AiMazeDatabase = instance ?: synchronized(this) {
            instance ?: Room.databaseBuilder(
                context.applicationContext,
                AiMazeDatabase::class.java,
                "ai-maze.db",
            )
                .addMigrations(migration1To2)
                .build()
                .also { instance = it }
        }
    }
}
