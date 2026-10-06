package com.akhil.aimaze.data.history

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface BenchmarkHistoryDao {
    @Query("SELECT * FROM benchmark_history ORDER BY createdAt DESC")
    fun observeAll(): Flow<List<BenchmarkHistoryEntity>>

    @Insert
    suspend fun insert(entity: BenchmarkHistoryEntity)

    @Query("DELETE FROM benchmark_history")
    suspend fun clearAll()
}
