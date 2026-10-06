package com.akhil.aimaze.domain

import com.akhil.aimaze.domain.benchmark.BenchmarkSuite
import com.akhil.aimaze.domain.maze.generation.DepthFirstMazeGenerator
import com.akhil.aimaze.domain.pathfinding.AStarPathfinder
import com.akhil.aimaze.domain.pathfinding.DijkstraPathfinder
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class LargeMazeSmokeTest {
    @Test(timeout = 5_000)
    fun twentyByTwentyMazeGeneratesAndSearchesWithoutPathologicalSlowdown() {
        val maze = DepthFirstMazeGenerator.generate(20, 20, seed = 2026L)
        val aStar = AStarPathfinder.solve(maze)
        val dijkstra = DijkstraPathfinder.solve(maze)

        assertTrue(aStar.found)
        assertTrue(dijkstra.found)
        assertEquals(aStar.pathLength, dijkstra.pathLength)
    }

    @Test(timeout = 10_000)
    fun benchmarkSuiteCompletesOnRepresentativeMaze() {
        val maze = DepthFirstMazeGenerator.generate(12, 12, seed = 42L)
        val report = BenchmarkSuite.run(
            maze = maze,
            qLearningEpisodes = 100,
            randomTrials = 10,
            seed = 42L,
        )

        assertEquals(2, report.deterministicSearches.size)
        assertTrue(report.reinforcementLearning.successRate in 0.0..1.0)
        assertTrue(report.stochasticBaseline.successRate in 0.0..1.0)
    }
}
