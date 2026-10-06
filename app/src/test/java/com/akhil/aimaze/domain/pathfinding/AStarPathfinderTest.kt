package com.akhil.aimaze.domain.pathfinding

import com.akhil.aimaze.domain.maze.Position
import com.akhil.aimaze.domain.maze.generation.DepthFirstMazeGenerator
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class AStarPathfinderTest {
    @Test
    fun findsValidPathAcrossGeneratedMaze() {
        val maze = DepthFirstMazeGenerator.generate(12, 12, seed = 99L)
        val result = AStarPathfinder.solve(maze)

        assertTrue(result.found)
        assertEquals(maze.start, result.path.first())
        assertEquals(maze.goal, result.path.last())
        assertTrue(result.nodesExplored > 0)
        assertValidPath(maze, result.path)
    }

    @Test
    fun sameMazeProducesSameOptimalPath() {
        val maze = DepthFirstMazeGenerator.generate(8, 8, seed = 5L)
        val first = AStarPathfinder.solve(maze)
        val second = AStarPathfinder.solve(maze)

        assertEquals(first.path, second.path)
        assertEquals(first.pathLength, second.pathLength)
    }

    @Test
    fun oneCellMazeHasZeroLengthPath() {
        val maze = DepthFirstMazeGenerator.generate(1, 1, seed = 1L)
        val result = AStarPathfinder.solve(maze)

        assertEquals(listOf(Position(0, 0)), result.path)
        assertEquals(0, result.pathLength)
    }

    private fun assertValidPath(maze: com.akhil.aimaze.domain.maze.Maze, path: List<Position>) {
        for (index in 0 until path.lastIndex) {
            assertTrue(path[index + 1] in maze.validNeighbors(path[index]))
        }
    }
}
