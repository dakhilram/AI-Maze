package com.akhil.aimaze.domain.benchmark

import com.akhil.aimaze.domain.maze.generation.DepthFirstMazeGenerator
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class BenchmarkSuiteTest {
    @Test
    fun deterministicAlgorithmsUseSameMazeAndAgreeOnOptimalLength() {
        val maze = DepthFirstMazeGenerator.generate(8, 8, seed = 11L)
        val report = BenchmarkSuite.run(
            maze = maze,
            qLearningEpisodes = 100,
            randomTrials = 5,
            seed = 1L,
        )

        val aStar = report.deterministicSearches.first { it.name == "A*" }
        val dijkstra = report.deterministicSearches.first { it.name == "Dijkstra" }
        assertEquals(aStar.pathLength, dijkstra.pathLength)
        assertTrue(aStar.nodesExplored > 0)
        assertTrue(dijkstra.nodesExplored > 0)
    }

    @Test
    fun reportKeepsAlgorithmFamiliesSeparate() {
        val maze = DepthFirstMazeGenerator.generate(6, 6, seed = 3L)
        val report = BenchmarkSuite.run(maze, qLearningEpisodes = 50, randomTrials = 3)

        assertEquals(2, report.deterministicSearches.size)
        assertEquals("Q-Learning", report.reinforcementLearning.name)
        assertEquals("Random", report.stochasticBaseline.name)
        assertTrue(report.reinforcementLearning.successRate in 0.0..1.0)
        assertTrue(report.stochasticBaseline.successRate in 0.0..1.0)
    }
}
