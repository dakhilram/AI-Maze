package com.akhil.aimaze.domain.pathfinding

import com.akhil.aimaze.domain.maze.Maze
import com.akhil.aimaze.domain.maze.Position
import java.util.PriorityQueue
import kotlin.math.abs

object AStarPathfinder {
    fun solve(maze: Maze): SearchResult {
        val started = System.nanoTime()
        if (maze.start == maze.goal) {
            return SearchResult(listOf(maze.start), 1, System.nanoTime() - started)
        }

        data class Node(val position: Position, val g: Int, val f: Int)
        val frontier = PriorityQueue<Node>(compareBy<Node> { it.f }.thenBy { it.g })
        val cameFrom = mutableMapOf<Position, Position>()
        val bestG = mutableMapOf(maze.start to 0)
        val closed = mutableSetOf<Position>()
        frontier += Node(maze.start, 0, heuristic(maze.start, maze.goal))

        while (frontier.isNotEmpty()) {
            val current = frontier.remove()
            if (!closed.add(current.position)) continue
            if (current.position == maze.goal) {
                return SearchResult(
                    path = reconstruct(cameFrom, maze.start, maze.goal),
                    nodesExplored = closed.size,
                    executionNanos = System.nanoTime() - started,
                )
            }

            for (neighbor in maze.validNeighbors(current.position)) {
                if (neighbor in closed) continue
                val tentative = current.g + 1
                if (tentative < bestG.getOrDefault(neighbor, Int.MAX_VALUE)) {
                    bestG[neighbor] = tentative
                    cameFrom[neighbor] = current.position
                    frontier += Node(
                        position = neighbor,
                        g = tentative,
                        f = tentative + heuristic(neighbor, maze.goal),
                    )
                }
            }
        }

        return SearchResult(emptyList(), closed.size, System.nanoTime() - started)
    }

    private fun heuristic(from: Position, to: Position): Int =
        abs(from.row - to.row) + abs(from.column - to.column)

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
