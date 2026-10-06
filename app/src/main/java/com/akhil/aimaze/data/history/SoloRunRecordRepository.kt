package com.akhil.aimaze.data.history

import kotlinx.coroutines.flow.Flow

class SoloRunRecordRepository(
    private val dao: SoloRunRecordDao,
) {
    fun observeAll(): Flow<List<SoloRunRecordEntity>> = dao.observeAll()

    suspend fun save(
        mode: String,
        rows: Int,
        columns: Int,
        mazeSeed: Long,
        moves: Int,
        elapsedMs: Long,
        stars: Int,
    ) {
        dao.insert(
            SoloRunRecordEntity(
                createdAt = System.currentTimeMillis(),
                mode = mode,
                rows = rows,
                columns = columns,
                mazeSeed = mazeSeed,
                moves = moves,
                elapsedMs = elapsedMs,
                stars = stars,
            ),
        )
    }

    suspend fun clearAll() = dao.clearAll()
}
