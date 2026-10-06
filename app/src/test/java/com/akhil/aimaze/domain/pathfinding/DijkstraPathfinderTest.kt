package com.akhil.aimaze.domain.pathfinding

import com.akhil.aimaze.domain.maze.generation.DepthFirstMazeGenerator
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class DijkstraPathfinderTest {
    @Test
    fun findsValidPath() {
        val maze = DepthFirstMazeGenerator.generate(12, 12, seed = 88L)
        val result = DijkstraPathfinder.solve(maze)

        assertTrue(result.found)
        assertEquals(maze.start, result.path.first())
        assertEquals(maze.goal, result.path.last())
        for (index in 0 until result.path.lastIndex) {
            assertTrue(result.path[index + 1] in maze.validNeighbors(result.path[index]))
        }
    }

    @Test
    fun matchesAStarOptimalPathLength() {
        val maze = DepthFirstMazeGenerator.generate(20, 20, seed = 2026L)
        val aStar = AStarPathfinder.solve(maze)
        val dijkstra = DijkstraPathfinder.solve(maze)

        assertEquals(aStar.pathLength, dijkstra.pathLength)
    }

    @Test
    fun oneCellMazeHasZeroLengthPath() {
        val maze = DepthFirstMazeGenerator.generate(1, 1, seed = 1L)
        val result = DijkstraPathfinder.solve(maze)

        assertTrue(result.found)
        assertEquals(0, result.pathLength)
    }
}
