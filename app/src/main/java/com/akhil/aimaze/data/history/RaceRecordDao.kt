package com.akhil.aimaze.data.history

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface RaceRecordDao {
    @Query("SELECT * FROM race_records ORDER BY createdAt DESC")
    fun observeAll(): Flow<List<RaceRecordEntity>>

    @Insert
    suspend fun insert(record: RaceRecordEntity)

    @Query("DELETE FROM race_records")
    suspend fun clearAll()
}
