package com.akhil.aimaze.domain.rl

import com.akhil.aimaze.domain.maze.Maze
import com.akhil.aimaze.domain.maze.MazeCell
import com.akhil.aimaze.domain.maze.Position
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Test

class QLearningTrainerTest {
    @Test
    fun learnsSimpleCorridorWithDeterministicSeed() {
        val maze = corridorMaze()
        val result = QLearningTrainer.train(
            maze = maze,
            config = QLearningConfig(
                episodes = 250,
                maxStepsPerEpisode = 30,
                epsilonDecay = 0.98,
                minimumEpsilon = 0.02,
            ),
            seed = 7L,
        )

        assertTrue(result.successRate > 0.8)
        assertEquals(
            listOf(Position(0, 0), Position(0, 1), Position(0, 2)),
            result.bestPath,
        )
        assertEquals(250, result.episodes.size)
    }

    @Test
    fun sameSeedAndConfigProduceSameMetrics() {
        val maze = corridorMaze()
        val config = QLearningConfig(episodes = 50, maxStepsPerEpisode = 20)

        val first = QLearningTrainer.train(maze, config, seed = 99L)
        val second = QLearningTrainer.train(maze, config, seed = 99L)

        assertEquals(first.episodes, second.episodes)
        assertEquals(first.qValues, second.qValues)
        assertEquals(first.bestPath, second.bestPath)
    }

    @Test
    fun epsilonNeverDecaysBelowMinimum() {
        val result = QLearningTrainer.train(
            maze = corridorMaze(),
            config = QLearningConfig(
                episodes = 100,
                initialEpsilon = 0.5,
                minimumEpsilon = 0.2,
                epsilonDecay = 0.5,
            ),
            seed = 1L,
        )

        assertTrue(result.finalEpsilon >= 0.2)
    }

    @Test
    fun oneCellMazeIsImmediateSuccess() {
        val maze = Maze(
            rows = 1,
            columns = 1,
            cells = listOf(listOf(MazeCell())),
            start = Position(0, 0),
            goal = Position(0, 0),
        )

        val result = QLearningTrainer.train(
            maze = maze,
            config = QLearningConfig(episodes = 5),
            seed = 1L,
        )

        assertEquals(1.0, result.successRate, 0.0)
        assertEquals(listOf(Position(0, 0)), result.bestPath)
        assertTrue(result.episodes.all { it.steps == 0 && it.success })
    }

    @Test
    fun invalidConfigurationIsRejected() {
        assertThrows(IllegalArgumentException::class.java) {
            QLearningConfig(episodes = 0)
        }
        assertThrows(IllegalArgumentException::class.java) {
            QLearningConfig(learningRate = 1.5)
        }
        assertThrows(IllegalArgumentException::class.java) {
            QLearningConfig(initialEpsilon = 0.1, minimumEpsilon = 0.2)
        }
    }

    private fun corridorMaze(): Maze = Maze(
        rows = 1,
        columns = 3,
        cells = listOf(
            listOf(
                MazeCell(eastWall = false),
                MazeCell(eastWall = false, westWall = false),
                MazeCell(westWall = false),
            ),
        ),
        start = Position(0, 0),
        goal = Position(0, 2),
    )
}
