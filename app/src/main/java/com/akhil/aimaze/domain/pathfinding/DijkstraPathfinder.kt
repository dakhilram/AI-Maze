package com.akhil.aimaze.domain.pathfinding

import com.akhil.aimaze.domain.maze.Maze
import com.akhil.aimaze.domain.maze.Position
import java.util.PriorityQueue

object DijkstraPathfinder {
    fun solve(maze: Maze): SearchResult {
        val started = System.nanoTime()
        if (maze.start == maze.goal) {
            return SearchResult(listOf(maze.start), 1, System.nanoTime() - started)
        }

        data class Node(val position: Position, val distance: Int)
        val frontier = PriorityQueue<Node>(compareBy<Node> { it.distance })
        val distance = mutableMapOf(maze.start to 0)
        val cameFrom = mutableMapOf<Position, Position>()
        val visited = mutableSetOf<Position>()
        frontier += Node(maze.start, 0)

        while (frontier.isNotEmpty()) {
            val current = frontier.remove()
            if (!visited.add(current.position)) continue
            if (current.position == maze.goal) {
                return SearchResult(
                    path = reconstruct(cameFrom, maze.start, maze.goal),
                    nodesExplored = visited.size,
                    executionNanos = System.nanoTime() - started,
                )
            }

            for (neighbor in maze.validNeighbors(current.position)) {
                if (neighbor in visited) continue
                val candidate = current.distance + 1
                if (candidate < distance.getOrDefault(neighbor, Int.MAX_VALUE)) {
                    distance[neighbor] = candidate
                    cameFrom[neighbor] = current.position
                    frontier += Node(neighbor, candidate)
                }
            }
        }

        return SearchResult(emptyList(), visited.size, System.nanoTime() - started)
    }

    private fun reconstruct(
        cameFrom: Map<Position, Position>,
        start: Position,
        goal: Position,
    ): List<Position> {
        val path = mutableListOf(goal)
        var current = goal
        while (current != start) {
            current = cameFrom[current] ?: return emptyList()
            path += current
        }
        return path.asReversed()
    }
}
