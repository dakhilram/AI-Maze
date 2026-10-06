package com.akhil.aimaze.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import com.akhil.aimaze.domain.maze.Maze
import com.akhil.aimaze.domain.maze.Position

@Composable
fun MazeBoard(
    maze: Maze,
    player: Position? = null,
    opponent: Position? = null,
    path: List<Position> = emptyList(),
    modifier: Modifier = Modifier,
) {
    val wallColor = MaterialTheme.colorScheme.onSurface
    val startColor = MaterialTheme.colorScheme.tertiary
    val goalColor = MaterialTheme.colorScheme.secondary
    val playerColor = MaterialTheme.colorScheme.primary
    val opponentColor = MaterialTheme.colorScheme.secondary
    val pathColor = MaterialTheme.colorScheme.primary
    val background = MaterialTheme.colorScheme.surfaceVariant

    Canvas(
        modifier = modifier.semantics {
            contentDescription = "Maze board with start, goal, walls, and agent state"
        },
    ) {
        drawRect(background)

        val cellWidth = size.width / maze.columns
        val cellHeight = size.height / maze.rows
        val wallWidth = (size.minDimension / 220f).coerceAtLeast(2f)

        fun center(position: Position) = Offset(
            x = (position.column + 0.5f) * cellWidth,
            y = (position.row + 0.5f) * cellHeight,
        )

        if (path.size > 1) {
            for (index in 0 until path.lastIndex) {
                drawLine(
                    color = pathColor.copy(alpha = 0.55f),
                    start = center(path[index]),
                    end = center(path[index + 1]),
                    strokeWidth = (size.minDimension / 140f).coerceAtLeast(3f),
                    cap = StrokeCap.Round,
                )
            }
        }

        val markerRadius = minOf(cellWidth, cellHeight) * 0.23f
        drawCircle(startColor, markerRadius, center(maze.start))
        drawCircle(goalColor, markerRadius, center(maze.goal))

        for (row in 0 until maze.rows) {
            for (column in 0 until maze.columns) {
                val position = Position(row, column)
                val cell = requireNotNull(maze.cellAt(position))
                val left = column * cellWidth
                val top = row * cellHeight
                val right = left + cellWidth
                val bottom = top + cellHeight

                if (cell.northWall) {
                    drawLine(wallColor, Offset(left, top), Offset(right, top), wallWidth, StrokeCap.Square)
                }
                if (cell.westWall) {
                    drawLine(wallColor, Offset(left, top), Offset(left, bottom), wallWidth, StrokeCap.Square)
                }
                if (row == maze.rows - 1 && cell.southWall) {
                    drawLine(wallColor, Offset(left, bottom), Offset(right, bottom), wallWidth, StrokeCap.Square)
                }
                if (column == maze.columns - 1 && cell.eastWall) {
                    drawLine(wallColor, Offset(right, top), Offset(right, bottom), wallWidth, StrokeCap.Square)
                }
            }
        }

        opponent?.let {
            drawCircle(
                color = opponentColor,
                radius = markerRadius * 0.56f,
                center = center(it),
            )
        }

        player?.let {
            drawCircle(
                color = playerColor,
                radius = markerRadius * 0.72f,
                center = center(it),
            )
        }
    }
}
