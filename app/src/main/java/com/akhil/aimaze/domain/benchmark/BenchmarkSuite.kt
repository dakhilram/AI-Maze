package com.akhil.aimaze.domain.benchmark

import com.akhil.aimaze.domain.baseline.RandomMazeAgent
import com.akhil.aimaze.domain.maze.Maze
import com.akhil.aimaze.domain.pathfinding.AStarPathfinder
import com.akhil.aimaze.domain.pathfinding.DijkstraPathfinder
import com.akhil.aimaze.domain.rl.QLearningConfig
import com.akhil.aimaze.domain.rl.QLearningTrainer

sealed interface BenchmarkResult {
    val name: String
}

data class DeterministicSearchBenchmark(
    override val name: String,
    val pathLength: Int,
    val nodesExplored: Int,
    val executionNanos: Long,
) : BenchmarkResult

data class ReinforcementLearningBenchmark(
    override val name: String = "Q-Learning",
    val trainingEpisodes: Int,
    val successRate: Double,
    val learnedPathLength: Int?,
    val finalEpsilon: Double,
    val trainingNanos: Long,
) : BenchmarkResult

data class StochasticBaselineBenchmark(
    override val name: String = "Random",
    val trials: Int,
    val successRate: Double,
    val averageSuccessfulSteps: Double?,
    val bestSuccessfulPathLength: Int?,
    val evaluationNanos: Long,
) : BenchmarkResult

data class BenchmarkReport(
    val deterministicSearches: List<DeterministicSearchBenchmark>,
    val reinforcementLearning: ReinforcementLearningBenchmark,
    val stochasticBaseline: StochasticBaselineBenchmark,
)

object BenchmarkSuite {
    fun run(
        maze: Maze,
        qLearningEpisodes: Int = 600,
        randomTrials: Int = 50,
        seed: Long = 42L,
    ): BenchmarkReport {
        require(qLearningEpisodes > 0)
        require(randomTrials > 0)

        val aStar = AStarPathfinder.solve(maze)
        val dijkstra = DijkstraPathfinder.solve(maze)

        val qStarted = System.nanoTime()
        val qResult = QLearningTrainer.train(
            maze = maze,
            config = QLearningConfig(
                episodes = qLearningEpisodes,
                maxStepsPerEpisode = maze.rows * maze.columns * 8,
            ),
            seed = seed,
        )
        val qElapsed = System.nanoTime() - qStarted

        val randomStarted = System.nanoTime()
        val random = RandomMazeAgent.evaluate(
            maze = maze,
            trials = randomTrials,
            maxSteps = maze.rows * maze.columns * 10,
            seed = seed,
        )
        val randomElapsed = System.nanoTime() - randomStarted

        return BenchmarkReport(
            deterministicSearches = listOf(
                DeterministicSearchBenchmark(
                    name = "A*",
                    pathLength = aStar.pathLength,
                    nodesExplored = aStar.nodesExplored,
                    executionNanos = aStar.executionNanos,
                ),
                DeterministicSearchBenchmark(
                    name = "Dijkstra",
                    pathLength = dijkstra.pathLength,
                    nodesExplored = dijkstra.nodesExplored,
                    executionNanos = dijkstra.executionNanos,
                ),
            ),
            reinforcementLearning = ReinforcementLearningBenchmark(
                trainingEpisodes = qLearningEpisodes,
                successRate = qResult.successRate,
                learnedPathLength = qResult.bestPath.takeIf { it.isNotEmpty() }?.size?.minus(1),
                finalEpsilon = qResult.finalEpsilon,
                trainingNanos = qElapsed,
            ),
            stochasticBaseline = StochasticBaselineBenchmark(
                trials = randomTrials,
                successRate = random.successRate,
                averageSuccessfulSteps = random.averageSuccessfulSteps,
                bestSuccessfulPathLength = random.bestSuccessfulPath.takeIf { it.isNotEmpty() }?.size?.minus(1),
                evaluationNanos = randomElapsed,
            ),
        )
    }
}
