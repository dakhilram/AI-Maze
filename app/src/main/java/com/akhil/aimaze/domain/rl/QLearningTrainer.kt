package com.akhil.aimaze.domain.rl

import com.akhil.aimaze.domain.maze.Direction
import com.akhil.aimaze.domain.maze.Maze
import com.akhil.aimaze.domain.maze.Position
import java.util.Random
import kotlin.math.max

data class QLearningConfig(
    val episodes: Int = 500,
    val learningRate: Double = 0.2,
    val discountFactor: Double = 0.95,
    val initialEpsilon: Double = 1.0,
    val minimumEpsilon: Double = 0.05,
    val epsilonDecay: Double = 0.992,
    val stepReward: Double = -1.0,
    val invalidMoveReward: Double = -4.0,
    val goalReward: Double = 100.0,
    val maxStepsPerEpisode: Int = 500,
) {
    init {
        require(episodes > 0)
        require(learningRate in 0.0..1.0)
        require(discountFactor in 0.0..1.0)
        require(initialEpsilon in 0.0..1.0)
        require(minimumEpsilon in 0.0..1.0)
        require(minimumEpsilon <= initialEpsilon)
        require(epsilonDecay in 0.0..1.0)
        require(maxStepsPerEpisode > 0)
    }
}

data class EpisodeMetric(
    val episode: Int,
    val reward: Double,
    val steps: Int,
    val success: Boolean,
    val epsilon: Double,
)

data class QLearningResult(
    val qValues: Map<Position, List<Double>>,
    val episodes: List<EpisodeMetric>,
    val successRate: Double,
    val finalEpsilon: Double,
    val bestPath: List<Position>,
)

object QLearningTrainer {
    fun train(
        maze: Maze,
        config: QLearningConfig = QLearningConfig(),
        seed: Long = 0L,
        onEpisode: ((EpisodeMetric) -> Unit)? = null,
    ): QLearningResult {
        val random = Random(seed)
        val qTable = mutableMapOf<Position, DoubleArray>()
        val metrics = ArrayList<EpisodeMetric>(config.episodes)
        var epsilon = config.initialEpsilon
        var successes = 0

        fun values(position: Position): DoubleArray =
            qTable.getOrPut(position) { DoubleArray(Direction.entries.size) }

        repeat(config.episodes) { index ->
            var state = maze.start
            var totalReward = 0.0
            var steps = 0
            var success = maze.start == maze.goal

            while (!success && steps < config.maxStepsPerEpisode) {
                val actionIndex = chooseAction(values(state), epsilon, random)
                val direction = Direction.entries[actionIndex]
                val moved = maze.move(state, direction)
                val next = moved ?: state
                val reward = when {
                    moved == null -> config.invalidMoveReward
                    next == maze.goal -> config.goalReward
                    else -> config.stepReward
                }

                val currentValues = values(state)
                val nextBest = values(next).maxOrNull() ?: 0.0
                currentValues[actionIndex] += config.learningRate * (
                    reward + config.discountFactor * nextBest - currentValues[actionIndex]
                )

                state = next
                totalReward += reward
                steps++
                success = state == maze.goal
            }

            if (success) successes++
            val metric = EpisodeMetric(
                episode = index + 1,
                reward = totalReward,
                steps = steps,
                success = success,
                epsilon = epsilon,
            )
            metrics += metric
            onEpisode?.invoke(metric)
            epsilon = max(config.minimumEpsilon, epsilon * config.epsilonDecay)
        }

        val immutableTable = qTable.mapValues { (_, value) -> value.toList() }
        return QLearningResult(
            qValues = immutableTable,
            episodes = metrics.toList(),
            successRate = successes.toDouble() / config.episodes,
            finalEpsilon = epsilon,
            bestPath = greedyPath(maze, immutableTable),
        )
    }

    private fun chooseAction(values: DoubleArray, epsilon: Double, random: Random): Int {
        if (random.nextDouble() < epsilon) return random.nextInt(Direction.entries.size)
        val best = values.maxOrNull() ?: 0.0
        val candidates = values.indices.filter { values[it] == best }
        return candidates[random.nextInt(candidates.size)]
    }

    private fun greedyPath(
        maze: Maze,
        qValues: Map<Position, List<Double>>,
    ): List<Position> {
        if (maze.start == maze.goal) return listOf(maze.start)

        val path = mutableListOf(maze.start)
        val visited = mutableSetOf(maze.start)
        var current = maze.start
        val limit = maze.rows * maze.columns * 4

        repeat(limit) {
            val values = qValues[current] ?: return emptyList()
            val orderedActions = values.indices.sortedByDescending { values[it] }
            val next = orderedActions.asSequence()
                .mapNotNull { maze.move(current, Direction.entries[it]) }
                .firstOrNull { it !in visited || it == maze.goal }
                ?: return emptyList()

            path += next
            if (next == maze.goal) return path
            visited += next
            current = next
        }
        return emptyList()
    }
}
