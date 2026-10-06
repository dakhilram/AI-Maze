package com.akhil.aimaze.data.history

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "benchmark_history")
data class BenchmarkHistoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val createdAt: Long,
    val rows: Int,
    val columns: Int,
    val mazeSeed: Long,
    val aStarPathLength: Int,
    val aStarNodesExplored: Int,
    val dijkstraPathLength: Int,
    val dijkstraNodesExplored: Int,
    val qLearningEpisodes: Int,
    val qLearningSuccessRate: Double,
    val qLearningPathLength: Int?,
    val randomTrials: Int,
    val randomSuccessRate: Double,
    val randomBestPathLength: Int?,
)
