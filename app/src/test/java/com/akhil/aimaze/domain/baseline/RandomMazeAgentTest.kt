package com.akhil.aimaze.domain.baseline

import com.akhil.aimaze.domain.maze.generation.DepthFirstMazeGenerator
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class RandomMazeAgentTest {
    @Test
    fun sameSeedProducesSameWalk() {
        val maze = DepthFirstMazeGenerator.generate(8, 8, seed = 9L)
        val first = RandomMazeAgent.run(maze, seed = 4L, maxSteps = 200)
        val second = RandomMazeAgent.run(maze, seed = 4L, maxSteps = 200)

        assertEquals(first.path, second.path)
        assertEquals(first.success, second.success)
    }

    @Test
    fun everyStepUsesValidMazeEdge() {
        val maze = DepthFirstMazeGenerator.generate(12, 12, seed = 12L)
        val run = RandomMazeAgent.run(maze, seed = 55L, maxSteps = 400)

        for (index in 0 until run.path.lastIndex) {
            assertTrue(run.path[index + 1] in maze.validNeighbors(run.path[index]))
        }
    }

    @Test
    fun respectsMaximumStepBudget() {
        val maze = DepthFirstMazeGenerator.generate(20, 20, seed = 1L)
        val run = RandomMazeAgent.run(maze, seed = 1L, maxSteps = 10)
        assertTrue(run.steps <= 10)
    }

    @Test
    fun evaluationReportsBoundedSuccessRate() {
        val maze = DepthFirstMazeGenerator.generate(8, 8, seed = 2L)
        val evaluation = RandomMazeAgent.evaluate(maze, trials = 10, maxSteps = 500, seed = 1L)

        assertEquals(10, evaluation.trials)
        assertTrue(evaluation.successRate in 0.0..1.0)
    }
}
