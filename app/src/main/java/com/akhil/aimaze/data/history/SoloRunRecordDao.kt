package com.akhil.aimaze.data.history

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface SoloRunRecordDao {
    @Query("SELECT * FROM solo_run_records ORDER BY createdAt DESC")
    fun observeAll(): Flow<List<SoloRunRecordEntity>>

    @Insert
    suspend fun insert(record: SoloRunRecordEntity)

    @Query("DELETE FROM solo_run_records")
    suspend fun clearAll()
}
