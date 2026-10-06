package com.akhil.aimaze.domain.maze

/**
 * Immutable wall state for one maze cell.
 *
 * Cells are fully enclosed by default, making partially specified test and
 * construction data safe rather than implicitly open.
 */
data class MazeCell(
    val northWall: Boolean = true,
    val eastWall: Boolean = true,
    val southWall: Boolean = true,
    val westWall: Boolean = true,
) {
    fun hasWall(direction: Direction): Boolean = when (direction) {
        Direction.NORTH -> northWall
        Direction.EAST -> eastWall
        Direction.SOUTH -> southWall
        Direction.WEST -> westWall
    }
}
