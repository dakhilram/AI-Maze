package com.akhil.aimaze.data.history

import com.akhil.aimaze.domain.benchmark.BenchmarkReport
import kotlinx.coroutines.flow.Flow

class BenchmarkHistoryRepository(
    private val dao: BenchmarkHistoryDao,
) {
    fun observeAll(): Flow<List<BenchmarkHistoryEntity>> = dao.observeAll()

    suspend fun save(
        report: BenchmarkReport,
        rows: Int,
        columns: Int,
        mazeSeed: Long,
    ) {
        val aStar = report.deterministicSearches.first { it.name == "A*" }
        val dijkstra = report.deterministicSearches.first { it.name == "Dijkstra" }
        dao.insert(
            BenchmarkHistoryEntity(
                createdAt = System.currentTimeMillis(),
                rows = rows,
                columns = columns,
                mazeSeed = mazeSeed,
                aStarPathLength = aStar.pathLength,
                aStarNodesExplored = aStar.nodesExplored,
                dijkstraPathLength = dijkstra.pathLength,
                dijkstraNodesExplored = dijkstra.nodesExplored,
                qLearningEpisodes = report.reinforcementLearning.trainingEpisodes,
                qLearningSuccessRate = report.reinforcementLearning.successRate,
                qLearningPathLength = report.reinforcementLearning.learnedPathLength,
                randomTrials = report.stochasticBaseline.trials,
                randomSuccessRate = report.stochasticBaseline.successRate,
                randomBestPathLength = report.stochasticBaseline.bestSuccessfulPathLength,
            ),
        )
    }

    suspend fun clearAll() = dao.clearAll()
}
