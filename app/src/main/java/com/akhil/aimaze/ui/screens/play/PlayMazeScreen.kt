package com.akhil.aimaze.ui.screens.play

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.akhil.aimaze.domain.maze.Direction
import com.akhil.aimaze.domain.maze.Maze
import com.akhil.aimaze.domain.maze.Position
import com.akhil.aimaze.domain.maze.generation.DepthFirstMazeGenerator
import com.akhil.aimaze.domain.play.MazePlayState

@Composable
fun PlayMazeScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var size by remember { mutableIntStateOf(8) }
    var seed by remember { mutableLongStateOf(42L) }
    var playState by remember(size, seed) {
        mutableStateOf(
            MazePlayState.initial(
                DepthFirstMazeGenerator.generate(size, size, seed),
            ),
        )
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text("Play Maze") },
                navigationIcon = {
                    OutlinedButton(onClick = onBack) { Text("Back") }
                },
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Text(
                text = "Solve a seeded maze yourself. Every move follows the same wall rules the AI will use later.",
                style = MaterialTheme.typography.bodyLarge,
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                listOf(8, 12, 16, 20).forEach { option ->
                    FilterChip(
                        selected = size == option,
                        onClick = { size = option },
                        label = { Text("${option}×${option}") },
                    )
                }
            }

            Text(
                text = "Seed: $seed  •  Moves: ${playState.moveCount}",
                style = MaterialTheme.typography.labelLarge,
            )

            MazeBoard(
                maze = playState.maze,
                player = playState.player,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f),
            )

            if (playState.completed) {
                Text(
                    text = "Maze solved in ${playState.moveCount} moves!",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary,
                )
            }

            DirectionControls(
                enabled = !playState.completed,
                onMove = { direction -> playState = playState.move(direction) },
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                OutlinedButton(onClick = { playState = playState.reset() }) {
                    Text("Restart")
                }
                Button(onClick = { seed += 1L }) {
                    Text("New maze")
                }
            }

            Text(
                text = "Tip: the seed makes a maze reproducible. The same size and seed always produce the same topology.",
                style = MaterialTheme.typography.bodySmall,
            )
            Spacer(Modifier.height(8.dp))
        }
    }
}

@Composable
private fun DirectionControls(
    enabled: Boolean,
    onMove: (Direction) -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        MoveButton("↑", "Move north", enabled) { onMove(Direction.NORTH) }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            MoveButton("←", "Move west", enabled) { onMove(Direction.WEST) }
            Box(Modifier.size(64.dp))
            MoveButton("→", "Move east", enabled) { onMove(Direction.EAST) }
        }
        MoveButton("↓", "Move south", enabled) { onMove(Direction.SOUTH) }
    }
}

@Composable
private fun MoveButton(
    label: String,
    description: String,
    enabled: Boolean,
    onClick: () -> Unit,
) {
    OutlinedButton(
        onClick = onClick,
        enabled = enabled,
        modifier = Modifier
            .size(64.dp)
            .semantics { contentDescription = description },
    ) {
        Text(label, style = MaterialTheme.typography.headlineSmall)
    }
}

@Composable
private fun MazeBoard(
    maze: Maze,
    player: Position,
    modifier: Modifier = Modifier,
) {
    val wallColor = MaterialTheme.colorScheme.onSurface
    val startColor = MaterialTheme.colorScheme.tertiary
    val goalColor = MaterialTheme.colorScheme.secondary
    val playerColor = MaterialTheme.colorScheme.primary
    val background = MaterialTheme.colorScheme.surfaceVariant

    Canvas(
        modifier = modifier.semantics {
            contentDescription = "Maze board with player, start, goal, and walls"
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

        drawCircle(
            color = playerColor,
            radius = markerRadius * 0.7f,
            center = center(player),
        )
    }
}
