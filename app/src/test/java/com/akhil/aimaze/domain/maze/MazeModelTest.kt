package com.akhil.aimaze.domain.maze

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Test

class MazeModelTest {
    @Test
    fun equalPositionsWorkAsSetKeys() {
        val position = Position(row = 2, column = 3)
        val equalPosition = Position(row = 2, column = 3)

        assertEquals(position, equalPosition)
        assertEquals(1, setOf(position, equalPosition).size)
    }

    @Test
    fun directionsExposeExpectedOffsets() {
        assertEquals(-1 to 0, Direction.NORTH.rowOffset to Direction.NORTH.columnOffset)
        assertEquals(0 to 1, Direction.EAST.rowOffset to Direction.EAST.columnOffset)
        assertEquals(1 to 0, Direction.SOUTH.rowOffset to Direction.SOUTH.columnOffset)
        assertEquals(0 to -1, Direction.WEST.rowOffset to Direction.WEST.columnOffset)
    }

    @Test
    fun directionsExposeTheirOpposites() {
        assertEquals(Direction.SOUTH, Direction.NORTH.opposite)
        assertEquals(Direction.WEST, Direction.EAST.opposite)
        assertEquals(Direction.NORTH, Direction.SOUTH.opposite)
        assertEquals(Direction.EAST, Direction.WEST.opposite)
    }

    @Test
    fun mazeChecksBounds() {
        val maze = sampleMaze()

        assertTrue(maze.isInside(Position(0, 0)))
        assertTrue(maze.isInside(Position(1, 1)))
        assertFalse(maze.isInside(Position(-1, 0)))
        assertFalse(maze.isInside(Position(0, -1)))
        assertFalse(maze.isInside(Position(2, 0)))
        assertFalse(maze.isInside(Position(0, 2)))
    }

    @Test
    fun cellLookupIsSafeOutsideBounds() {
        val maze = sampleMaze()

        assertEquals(sampleCells()[0][0], maze.cellAt(Position(0, 0)))
        assertNull(maze.cellAt(Position(-1, 0)))
    }

    @Test
    fun wallBlocksMovement() {
        val maze = sampleMaze()
        val start = Position(0, 0)

        assertFalse(maze.canMove(start, Direction.SOUTH))
        assertNull(maze.move(start, Direction.SOUTH))
    }

    @Test
    fun openSharedEdgeAllowsMovementInBothDirections() {
        val maze = sampleMaze()

        assertEquals(Position(0, 1), maze.move(Position(0, 0), Direction.EAST))
        assertEquals(Position(0, 0), maze.move(Position(0, 1), Direction.WEST))
    }

    @Test
    fun movementCannotLeaveMazeBounds() {
        val maze = sampleMaze()

        assertFalse(maze.canMove(Position(0, 0), Direction.NORTH))
        assertNull(maze.move(Position(0, 0), Direction.NORTH))
        assertNull(maze.move(Position(-1, 0), Direction.SOUTH))
    }

    @Test
    fun validNeighborsReturnsOnlyReachableAdjacentPositions() {
        val maze = sampleMaze()

        assertEquals(
            listOf(Position(0, 1), Position(1, 0)),
            maze.validNeighbors(Position(1, 1)),
        )
        assertTrue(maze.validNeighbors(Position(-1, 0)).isEmpty())
    }

    @Test
    fun dimensionsMustBePositive() {
        assertThrows(IllegalArgumentException::class.java) {
            Maze(
                rows = 0,
                columns = 1,
                cells = emptyList(),
                start = Position(0, 0),
                goal = Position(0, 0),
            )
        }
        assertThrows(IllegalArgumentException::class.java) {
            Maze(
                rows = 1,
                columns = 0,
                cells = listOf(emptyList()),
                start = Position(0, 0),
                goal = Position(0, 0),
            )
        }
    }

    @Test
    fun cellGridMustMatchDimensions() {
        assertThrows(IllegalArgumentException::class.java) {
            Maze(
                rows = 2,
                columns = 2,
                cells = listOf(listOf(MazeCell(), MazeCell())),
                start = Position(0, 0),
                goal = Position(1, 1),
            )
        }
        assertThrows(IllegalArgumentException::class.java) {
            Maze(
                rows = 2,
                columns = 2,
                cells = listOf(
                    listOf(MazeCell(), MazeCell()),
                    listOf(MazeCell()),
                ),
                start = Position(0, 0),
                goal = Position(1, 1),
            )
        }
    }

    @Test
    fun startAndGoalMustBeInsideMaze() {
        assertThrows(IllegalArgumentException::class.java) {
            sampleMaze(start = Position(-1, 0))
        }
        assertThrows(IllegalArgumentException::class.java) {
            sampleMaze(goal = Position(2, 1))
        }
    }

    @Test
    fun adjacentCellsMustAgreeAboutSharedWalls() {
        val inconsistentCells = listOf(
            listOf(
                MazeCell(eastWall = false),
                MazeCell(westWall = true),
            ),
        )

        assertThrows(IllegalArgumentException::class.java) {
            Maze(
                rows = 1,
                columns = 2,
                cells = inconsistentCells,
                start = Position(0, 0),
                goal = Position(0, 1),
            )
        }
    }

    @Test
    fun mazeDefensivelyCopiesItsCellGrid() {
        val topRow = sampleCells()[0].toMutableList()
        val bottomRow = sampleCells()[1].toMutableList()
        val mutableGrid = mutableListOf<List<MazeCell>>(topRow, bottomRow)
        val maze = Maze(
            rows = 2,
            columns = 2,
            cells = mutableGrid,
            start = Position(0, 0),
            goal = Position(1, 1),
        )

        topRow[0] = MazeCell()
        mutableGrid.clear()

        assertEquals(false, maze.cellAt(Position(0, 0))?.eastWall)
        assertEquals(Position(0, 1), maze.move(Position(0, 0), Direction.EAST))
    }

    private fun sampleMaze(
        start: Position = Position(0, 0),
        goal: Position = Position(1, 1),
    ): Maze = Maze(
        rows = 2,
        columns = 2,
        cells = sampleCells(),
        start = start,
        goal = goal,
    )

    private fun sampleCells(): List<List<MazeCell>> = listOf(
        listOf(
            MazeCell(
                northWall = true,
                eastWall = false,
                southWall = true,
                westWall = true,
            ),
            MazeCell(
                northWall = true,
                eastWall = true,
                southWall = false,
                westWall = false,
            ),
        ),
        listOf(
            MazeCell(
                northWall = true,
                eastWall = false,
                southWall = true,
                westWall = true,
            ),
            MazeCell(
                northWall = false,
                eastWall = true,
                southWall = true,
                westWall = false,
            ),
        ),
    )
}
