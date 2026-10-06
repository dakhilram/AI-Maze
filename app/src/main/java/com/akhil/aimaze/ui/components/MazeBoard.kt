package com.akhil.aimaze.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
    val startColor = MaterialTheme.colorScheme.onSurfaceVariant
    val goalColor = MaterialTheme.colorScheme.tertiary
    val playerColor = MaterialTheme.colorScheme.primary
    val opponentColor = MaterialTheme.colorScheme.secondary
    val pathColor = MaterialTheme.colorScheme.primary
    val background = MaterialTheme.colorScheme.surfaceVariant

    val playerRow by animateFloatAsState(
        targetValue = player?.row?.toFloat() ?: 0f,
        animationSpec = tween(110, easing = FastOutSlowInEasing),
        label = "playerRow",
    )
    val playerColumn by animateFloatAsState(
        targetValue = player?.column?.toFloat() ?: 0f,
        animationSpec = tween(110, easing = FastOutSlowInEasing),
        label = "playerColumn",
    )
    val opponentRow by animateFloatAsState(
        targetValue = opponent?.row?.toFloat() ?: 0f,
        animationSpec = tween(180, easing = FastOutSlowInEasing),
        label = "opponentRow",
    )
    val opponentColumn by animateFloatAsState(
        targetValue = opponent?.column?.toFloat() ?: 0f,
        animationSpec = tween(180, easing = FastOutSlowInEasing),
        label = "opponentColumn",
    )

    val pulseTransition = rememberInfiniteTransition(label = "goalPulse")
    val goalPulse by pulseTransition.animateFloat(
        initialValue = 0.82f,
        targetValue = 1.12f,
        animationSpec = infiniteRepeatable(
            animation = tween(700, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "goalPulseValue",
    )

    Canvas(
        modifier = modifier.semantics {
            contentDescription = "Maze board"
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
        fun animatedCenter(row: Float, column: Float) = Offset(
            x = (column + 0.5f) * cellWidth,
            y = (row + 0.5f) * cellHeight,
        )

        if (path.size > 1) {
            for (index in 0 until path.lastIndex) {
                drawLine(
                    color = pathColor.copy(alpha = 0.42f),
                    start = center(path[index]),
                    end = center(path[index + 1]),
                    strokeWidth = (size.minDimension / 145f).coerceAtLeast(3f),
                    cap = StrokeCap.Round,
                )
            }
        }

        val markerRadius = minOf(cellWidth, cellHeight) * 0.23f
        drawCircle(startColor.copy(alpha = 0.35f), markerRadius * 0.90f, center(maze.start))
        drawCircle(startColor.copy(alpha = 0.85f), markerRadius * 0.48f, center(maze.start))
        drawCircle(goalColor.copy(alpha = 0.16f), markerRadius * 1.95f * goalPulse, center(maze.goal))
        drawCircle(goalColor.copy(alpha = 0.34f), markerRadius * 1.45f * goalPulse, center(maze.goal))
        drawCircle(goalColor, markerRadius * 0.82f * goalPulse, center(maze.goal))

        for (row in 0 until maze.rows) {
            for (column in 0 until maze.columns) {
                val position = Position(row, column)
                val cell = requireNotNull(maze.cellAt(position))
                val left = column * cellWidth
                val top = row * cellHeight
                val right = left + cellWidth
                val bottom = top + cellHeight

                if (cell.northWall) drawLine(wallColor, Offset(left, top), Offset(right, top), wallWidth, StrokeCap.Round)
                if (cell.westWall) drawLine(wallColor, Offset(left, top), Offset(left, bottom), wallWidth, StrokeCap.Round)
                if (row == maze.rows - 1 && cell.southWall) drawLine(wallColor, Offset(left, bottom), Offset(right, bottom), wallWidth, StrokeCap.Round)
                if (column == maze.columns - 1 && cell.eastWall) drawLine(wallColor, Offset(right, top), Offset(right, bottom), wallWidth, StrokeCap.Round)
            }
        }

        opponent?.let {
            drawCircle(
                color = opponentColor,
                radius = markerRadius * 0.55f,
                center = animatedCenter(opponentRow, opponentColumn),
            )
        }

        player?.let {
            drawCircle(
                color = playerColor.copy(alpha = 0.25f),
                radius = markerRadius,
                center = animatedCenter(playerRow, playerColumn),
            )
            drawCircle(
                color = playerColor,
                radius = markerRadius * 0.68f,
                center = animatedCenter(playerRow, playerColumn),
            )
        }
    }
}
