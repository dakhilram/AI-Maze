package com.akhil.aimaze.domain.baseline

import com.akhil.aimaze.domain.maze.Maze
import com.akhil.aimaze.domain.maze.Position
import java.util.Random

data class RandomRunResult(
    val path: List<Position>,
    val steps: Int,
    val success: Boolean,
    val uniqueVisited: Int,
    val executionNanos: Long,
)

data class RandomEvaluation(
    val trials: Int,
    val successes: Int,
    val successRate: Double,
    val averageSuccessfulSteps: Double?,
    val bestSuccessfulPath: List<Position>,
)

object RandomMazeAgent {
    fun run(
        maze: Maze,
        seed: Long,
        maxSteps: Int = maze.rows * maze.columns * 10,
    ): RandomRunResult {
        require(maxSteps > 0)
        val started = System.nanoTime()
        val random = Random(seed)
        var current = maze.start
        val path = mutableListOf(current)
        val visited = mutableSetOf(current)

        repeat(maxSteps) {
            if (current == maze.goal) {
                return RandomRunResult(
                    path = path.toList(),
                    steps = path.size - 1,
                    success = true,
                    uniqueVisited = visited.size,
                    executionNanos = System.nanoTime() - started,
                )
            }
            val neighbors = maze.validNeighbors(current)
            if (neighbors.isEmpty()) return@repeat
            current = neighbors[random.nextInt(neighbors.size)]
            path += current
            visited += current
        }

        return RandomRunResult(
            path = path.toList(),
            steps = path.size - 1,
            success = current == maze.goal,
            uniqueVisited = visited.size,
            executionNanos = System.nanoTime() - started,
        )
    }

    fun evaluate(
        maze: Maze,
        trials: Int = 50,
        maxSteps: Int = maze.rows * maze.columns * 10,
        seed: Long = 0L,
    ): RandomEvaluation {
        require(trials > 0)
        val successful = buildList {
            repeat(trials) { index ->
                val run = run(maze, seed = seed + index, maxSteps = maxSteps)
                if (run.success) add(run)
            }
        }
        return RandomEvaluation(
            trials = trials,
            successes = successful.size,
            successRate = successful.size.toDouble() / trials,
            averageSuccessfulSteps = successful.takeIf { it.isNotEmpty() }?.map { it.steps }?.average(),
            bestSuccessfulPath = successful.minByOrNull { it.steps }?.path.orEmpty(),
        )
    }
}
