package com.akhil.aimaze.domain.play

import com.akhil.aimaze.domain.maze.Direction
import com.akhil.aimaze.domain.maze.Maze
import com.akhil.aimaze.domain.maze.MazeCell
import com.akhil.aimaze.domain.maze.Position
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Test

class MazePlayStateTest {
    @Test
    fun startsAtMazeStartWithZeroMoves() {
        val state = MazePlayState.initial(horizontalMaze())

        assertEquals(Position(0, 0), state.player)
        assertEquals(0, state.moveCount)
        assertFalse(state.completed)
    }

    @Test
    fun legalMoveAdvancesAndCountsOnce() {
        val state = MazePlayState.initial(horizontalMaze())

        val moved = state.move(Direction.EAST)

        assertEquals(Position(0, 1), moved.player)
        assertEquals(1, moved.moveCount)
        assertFalse(moved.completed)
    }

    @Test
    fun blockedMoveDoesNotChangeState() {
        val state = MazePlayState.initial(horizontalMaze())

        val blocked = state.move(Direction.NORTH)

        assertSame(state, blocked)
        assertEquals(0, blocked.moveCount)
    }

    @Test
    fun reachingGoalCompletesRun() {
        val completed = MazePlayState.initial(horizontalMaze())
            .move(Direction.EAST)
            .move(Direction.EAST)

        assertEquals(Position(0, 2), completed.player)
        assertEquals(2, completed.moveCount)
        assertTrue(completed.completed)
    }

    @Test
    fun completedRunIgnoresFurtherMoves() {
        val completed = MazePlayState.initial(horizontalMaze())
            .move(Direction.EAST)
            .move(Direction.EAST)

        assertSame(completed, completed.move(Direction.WEST))
    }

    @Test
    fun resetReturnsToInitialState() {
        val moved = MazePlayState.initial(horizontalMaze()).move(Direction.EAST)

        val reset = moved.reset()

        assertEquals(Position(0, 0), reset.player)
        assertEquals(0, reset.moveCount)
        assertFalse(reset.completed)
    }

    @Test
    fun oneCellMazeStartsCompleted() {
        val maze = Maze(
            rows = 1,
            columns = 1,
            cells = listOf(listOf(MazeCell())),
            start = Position(0, 0),
            goal = Position(0, 0),
        )

        val state = MazePlayState.initial(maze)

        assertTrue(state.completed)
        assertEquals(0, state.moveCount)
    }

    private fun horizontalMaze(): Maze = Maze(
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
