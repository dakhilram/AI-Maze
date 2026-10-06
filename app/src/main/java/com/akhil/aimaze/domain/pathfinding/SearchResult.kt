package com.akhil.aimaze.domain.pathfinding

import com.akhil.aimaze.domain.maze.Position

data class SearchResult(
    val path: List<Position>,
    val nodesExplored: Int,
    val executionNanos: Long,
) {
    val found: Boolean get() = path.isNotEmpty()
    val pathLength: Int get() = if (path.isEmpty()) 0 else path.size - 1
}
