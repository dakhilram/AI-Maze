package com.akhil.aimaze.domain.play

import com.akhil.aimaze.domain.maze.Direction
import com.akhil.aimaze.domain.maze.Maze
import com.akhil.aimaze.domain.maze.Position

/**
 * Immutable state for a human-controlled maze run.
 *
 * Movement is delegated to [Maze], so wall and bounds rules have a single source of truth.
 */
data class MazePlayState private constructor(
    val maze: Maze,
    val player: Position,
    val moveCount: Int,
    val completed: Boolean,
) {
    fun move(direction: Direction): MazePlayState {
        if (completed) return this

        val next = maze.move(player, direction) ?: return this
        return copy(
            player = next,
            moveCount = moveCount + 1,
            completed = next == maze.goal,
        )
    }

    fun reset(): MazePlayState = initial(maze)

    companion object {
        fun initial(maze: Maze): MazePlayState = MazePlayState(
            maze = maze,
            player = maze.start,
            moveCount = 0,
            completed = maze.start == maze.goal,
        )
    }
}
