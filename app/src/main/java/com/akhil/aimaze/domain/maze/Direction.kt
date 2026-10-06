package com.akhil.aimaze.domain.maze

/** A cardinal movement direction and its grid-coordinate offset. */
enum class Direction(
    val rowOffset: Int,
    val columnOffset: Int,
) {
    NORTH(rowOffset = -1, columnOffset = 0),
    EAST(rowOffset = 0, columnOffset = 1),
    SOUTH(rowOffset = 1, columnOffset = 0),
    WEST(rowOffset = 0, columnOffset = -1),
    ;

    val opposite: Direction
        get() = when (this) {
            NORTH -> SOUTH
            EAST -> WEST
            SOUTH -> NORTH
            WEST -> EAST
        }
}
