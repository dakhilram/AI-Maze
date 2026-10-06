package com.akhil.aimaze.domain.maze.generation

import com.akhil.aimaze.domain.maze.Direction
import com.akhil.aimaze.domain.maze.Maze
import com.akhil.aimaze.domain.maze.MazeCell
import com.akhil.aimaze.domain.maze.Position
import java.util.ArrayDeque
import java.util.Random

/** Generates perfect rectangular mazes using iterative randomized depth-first search. */
object DepthFirstMazeGenerator {
    fun generate(
        rows: Int,
        columns: Int,
        seed: Long,
        start: Position = Position(row = 0, column = 0),
        goal: Position = Position(row = rows - 1, column = columns - 1),
    ): Maze {
        require(rows > 0) { "Maze rows must be positive." }
        require(columns > 0) { "Maze columns must be positive." }
        require(start.isInside(rows, columns)) { "Start position must be inside the maze." }
        require(goal.isInside(rows, columns)) { "Goal position must be inside the maze." }

        val cells = Array(rows) { Array(columns) { MutableCell() } }
        val visited = Array(rows) { BooleanArray(columns) }
        val stack = ArrayDeque<Position>()
        val random = Random(seed)

        val firstPosition = Position(row = 0, column = 0)
        visited[firstPosition.row][firstPosition.column] = true
        stack.addLast(firstPosition)

        while (stack.isNotEmpty()) {
            val current = requireNotNull(stack.peekLast())
            val availableDirections = Direction.entries.filter { direction ->
                val neighbor = current.adjacent(direction)
                neighbor.isInside(rows, columns) && !visited[neighbor.row][neighbor.column]
            }

            if (availableDirections.isEmpty()) {
                stack.removeLast()
                continue
            }

            val direction = availableDirections[random.nextInt(availableDirections.size)]
            val neighbor = current.adjacent(direction)

            cells[current.row][current.column].removeWall(direction)
            cells[neighbor.row][neighbor.column].removeWall(direction.opposite)
            visited[neighbor.row][neighbor.column] = true
            stack.addLast(neighbor)
        }

        val immutableCells = List(rows) { row ->
            List(columns) { column -> cells[row][column].toMazeCell() }
        }

        return Maze(
            rows = rows,
            columns = columns,
            cells = immutableCells,
            start = start,
            goal = goal,
        )
    }

    private fun Position.adjacent(direction: Direction): Position = Position(
        row = row + direction.rowOffset,
        column = column + direction.columnOffset,
    )

    private fun Position.isInside(rows: Int, columns: Int): Boolean =
        row in 0 until rows && column in 0 until columns

    private class MutableCell {
        private var northWall: Boolean = true
        private var eastWall: Boolean = true
        private var southWall: Boolean = true
        private var westWall: Boolean = true

        fun removeWall(direction: Direction) {
            when (direction) {
                Direction.NORTH -> northWall = false
                Direction.EAST -> eastWall = false
                Direction.SOUTH -> southWall = false
                Direction.WEST -> westWall = false
            }
        }

        fun toMazeCell(): MazeCell = MazeCell(
            northWall = northWall,
            eastWall = eastWall,
            southWall = southWall,
            westWall = westWall,
        )
    }
}
