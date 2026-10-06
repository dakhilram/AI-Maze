package com.akhil.aimaze.domain.maze.generation

import com.akhil.aimaze.domain.maze.Direction
import com.akhil.aimaze.domain.maze.Maze
import com.akhil.aimaze.domain.maze.MazeCell
import com.akhil.aimaze.domain.maze.Position
import java.util.ArrayDeque
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Test

class DepthFirstMazeGeneratorTest {
    @Test
    fun sameSeedAndDimensionsProduceIdenticalTopology() {
        val first = DepthFirstMazeGenerator.generate(rows = 10, columns = 14, seed = 42L)
        val second = DepthFirstMazeGenerator.generate(rows = 10, columns = 14, seed = 42L)

        assertEquals(topologyOf(first), topologyOf(second))
    }

    @Test
    fun differentSeedsProduceDifferentTopology() {
        val first = DepthFirstMazeGenerator.generate(rows = 12, columns = 12, seed = 1L)
        val second = DepthFirstMazeGenerator.generate(rows = 12, columns = 12, seed = 2L)

        assertNotEquals(topologyOf(first), topologyOf(second))
    }

    @Test
    fun generatedMazeHasRequestedDimensions() {
        val maze = DepthFirstMazeGenerator.generate(rows = 7, columns = 11, seed = 7L)

        assertEquals(7, maze.rows)
        assertEquals(11, maze.columns)
        assertEquals(77, topologyOf(maze).size)
    }

    @Test
    fun customStartAndGoalArePreserved() {
        val start = Position(row = 2, column = 3)
        val goal = Position(row = 4, column = 1)

        val maze = DepthFirstMazeGenerator.generate(
            rows = 5,
            columns = 6,
            seed = 99L,
            start = start,
            goal = goal,
        )

        assertEquals(start, maze.start)
        assertEquals(goal, maze.goal)
    }

    @Test
    fun endpointsDoNotChangeSeededTopology() {
        val defaultEndpoints = DepthFirstMazeGenerator.generate(
            rows = 6,
            columns = 8,
            seed = 123L,
        )
        val customEndpoints = DepthFirstMazeGenerator.generate(
            rows = 6,
            columns = 8,
            seed = 123L,
            start = Position(2, 2),
            goal = Position(4, 5),
        )

        assertEquals(topologyOf(defaultEndpoints), topologyOf(customEndpoints))
    }

    @Test
    fun everyGeneratedCellIsReachable() {
        val maze = DepthFirstMazeGenerator.generate(rows = 13, columns = 9, seed = 2026L)

        assertEquals(maze.rows * maze.columns, reachablePositionsIn(maze).size)
    }

    @Test
    fun adjacentCellsAgreeAboutEverySharedWall() {
        val maze = DepthFirstMazeGenerator.generate(rows = 9, columns = 13, seed = 81L)

        for (row in 0 until maze.rows) {
            for (column in 0 until maze.columns) {
                val position = Position(row, column)
                for (direction in Direction.entries) {
                    val neighbor = position.adjacent(direction)
                    if (!maze.isInside(neighbor)) {
                        assertFalse(maze.canMove(position, direction))
                        continue
                    }

                    val currentWall = requireNotNull(maze.cellAt(position)).hasWall(direction)
                    val neighborWall = requireNotNull(maze.cellAt(neighbor))
                        .hasWall(direction.opposite)

                    assertEquals(currentWall, neighborWall)
                    assertEquals(
                        maze.canMove(position, direction),
                        maze.canMove(neighbor, direction.opposite),
                    )
                }
            }
        }
    }

    @Test
    fun generatedMazeHasExactlyOneLessOpenConnectionThanCells() {
        val maze = DepthFirstMazeGenerator.generate(rows = 15, columns = 10, seed = 15L)

        assertEquals(maze.rows * maze.columns - 1, openConnectionCount(maze))
    }

    @Test
    fun oneByOneMazeWorks() {
        val maze = DepthFirstMazeGenerator.generate(rows = 1, columns = 1, seed = 5L)

        assertEquals(Position(0, 0), maze.start)
        assertEquals(Position(0, 0), maze.goal)
        assertTrue(maze.validNeighbors(Position(0, 0)).isEmpty())
        assertEquals(0, openConnectionCount(maze))
        assertEquals(setOf(Position(0, 0)), reachablePositionsIn(maze))
    }

    @Test
    fun rectangularMazeIsConnectedAndAcyclic() {
        val maze = DepthFirstMazeGenerator.generate(rows = 4, columns = 17, seed = 4017L)

        assertEquals(68, reachablePositionsIn(maze).size)
        assertEquals(67, openConnectionCount(maze))
    }

    @Test
    fun commonMazeSizesGenerateSuccessfully() {
        val sizes = listOf(8, 12, 16, 20)

        for (size in sizes) {
            val maze = DepthFirstMazeGenerator.generate(
                rows = size,
                columns = size,
                seed = size.toLong(),
            )

            assertEquals(size * size, reachablePositionsIn(maze).size)
            assertEquals(size * size - 1, openConnectionCount(maze))
        }
    }

    @Test
    fun invalidDimensionsAreRejected() {
        assertThrows(IllegalArgumentException::class.java) {
            DepthFirstMazeGenerator.generate(rows = 0, columns = 5, seed = 1L)
        }
        assertThrows(IllegalArgumentException::class.java) {
            DepthFirstMazeGenerator.generate(rows = 5, columns = 0, seed = 1L)
        }
        assertThrows(IllegalArgumentException::class.java) {
            DepthFirstMazeGenerator.generate(rows = -1, columns = 5, seed = 1L)
        }
    }

    @Test
    fun invalidEndpointsAreRejected() {
        assertThrows(IllegalArgumentException::class.java) {
            DepthFirstMazeGenerator.generate(
                rows = 5,
                columns = 5,
                seed = 1L,
                start = Position(-1, 0),
            )
        }
        assertThrows(IllegalArgumentException::class.java) {
            DepthFirstMazeGenerator.generate(
                rows = 5,
                columns = 5,
                seed = 1L,
                goal = Position(5, 4),
            )
        }
    }

    private fun topologyOf(maze: Maze): List<MazeCell> = buildList {
        for (row in 0 until maze.rows) {
            for (column in 0 until maze.columns) {
                add(requireNotNull(maze.cellAt(Position(row, column))))
            }
        }
    }

    private fun reachablePositionsIn(maze: Maze): Set<Position> {
        val visited = mutableSetOf(maze.start)
        val pending = ArrayDeque<Position>()
        pending.addLast(maze.start)

        while (pending.isNotEmpty()) {
            val current = pending.removeFirst()
            for (neighbor in maze.validNeighbors(current)) {
                if (visited.add(neighbor)) {
                    pending.addLast(neighbor)
                }
            }
        }

        return visited
    }

    private fun openConnectionCount(maze: Maze): Int {
        var connections = 0
        for (row in 0 until maze.rows) {
            for (column in 0 until maze.columns) {
                val position = Position(row, column)
                if (maze.canMove(position, Direction.EAST)) connections++
                if (maze.canMove(position, Direction.SOUTH)) connections++
            }
        }
        return connections
    }

    private fun Position.adjacent(direction: Direction): Position = Position(
        row = row + direction.rowOffset,
        column = column + direction.columnOffset,
    )
}
