package com.akhil.aimaze.domain.maze

/**
 * An immutable rectangular maze.
 *
 * The supplied cell grid is defensively copied. Construction validates the
 * dimensions, endpoints, grid shape, and every wall shared by adjacent cells.
 */
class Maze(
    val rows: Int,
    val columns: Int,
    cells: List<List<MazeCell>>,
    val start: Position,
    val goal: Position,
) {
    private val cellGrid: List<List<MazeCell>> = cells.map { row -> row.toList() }

    init {
        require(rows > 0) { "Maze rows must be positive." }
        require(columns > 0) { "Maze columns must be positive." }
        require(cellGrid.size == rows) {
            "Cell grid must contain exactly $rows rows."
        }
        require(cellGrid.all { row -> row.size == columns }) {
            "Every cell-grid row must contain exactly $columns cells."
        }
        require(isInside(start)) { "Start position must be inside the maze." }
        require(isInside(goal)) { "Goal position must be inside the maze." }

        validateSharedWalls()
    }

    fun isInside(position: Position): Boolean =
        position.row in 0 until rows && position.column in 0 until columns

    fun cellAt(position: Position): MazeCell? =
        if (isInside(position)) cellGrid[position.row][position.column] else null

    fun canMove(from: Position, direction: Direction): Boolean {
        val currentCell = cellAt(from) ?: return false
        val destination = adjacentPosition(from, direction)
        val destinationCell = cellAt(destination) ?: return false

        return !currentCell.hasWall(direction) &&
            !destinationCell.hasWall(direction.opposite)
    }

    fun move(from: Position, direction: Direction): Position? =
        adjacentPosition(from, direction).takeIf { canMove(from, direction) }

    fun validNeighbors(position: Position): List<Position> =
        Direction.entries.mapNotNull { direction -> move(position, direction) }

    private fun adjacentPosition(position: Position, direction: Direction): Position =
        Position(
            row = position.row + direction.rowOffset,
            column = position.column + direction.columnOffset,
        )

    private fun validateSharedWalls() {
        for (row in 0 until rows) {
            for (column in 0 until columns) {
                val position = Position(row, column)
                validateSharedWall(position, Direction.EAST)
                validateSharedWall(position, Direction.SOUTH)
            }
        }
    }

    private fun validateSharedWall(position: Position, direction: Direction) {
        val neighbor = adjacentPosition(position, direction)
        if (!isInside(neighbor)) return

        val wallExists = cellGrid[position.row][position.column].hasWall(direction)
        val neighborWallExists = cellGrid[neighbor.row][neighbor.column]
            .hasWall(direction.opposite)

        require(wallExists == neighborWallExists) {
            "Adjacent cells at $position and $neighbor disagree about their shared wall."
        }
    }
}
