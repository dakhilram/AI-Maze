package com.akhil.aimaze.data.history

import kotlinx.coroutines.flow.Flow

class RaceRecordRepository(
    private val dao: RaceRecordDao,
) {
    fun observeAll(): Flow<List<RaceRecordEntity>> = dao.observeAll()

    suspend fun save(
        rows: Int,
        columns: Int,
        mazeSeed: Long,
        winner: String,
        playerMoves: Int,
        botSteps: Int,
    ) {
        dao.insert(
            RaceRecordEntity(
                createdAt = System.currentTimeMillis(),
                rows = rows,
                columns = columns,
                mazeSeed = mazeSeed,
                winner = winner,
                playerMoves = playerMoves,
                botSteps = botSteps,
            ),
        )
    }

    suspend fun clearAll() = dao.clearAll()
}
