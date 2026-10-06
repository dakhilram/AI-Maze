@file:OptIn(ExperimentalMaterial3Api::class)

package com.akhil.aimaze.ui.screens.play

import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.akhil.aimaze.domain.maze.Direction
import com.akhil.aimaze.domain.maze.generation.DepthFirstMazeGenerator
import com.akhil.aimaze.domain.play.MazePlayState
import com.akhil.aimaze.ui.components.MazeBoard
import kotlin.math.abs

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayMazeScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var size by remember { mutableIntStateOf(8) }
    var seed by remember { mutableLongStateOf(42L) }
    var seedText by remember { mutableStateOf("42") }
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
                text = "Solve the maze by swiping directly on the board. Every swipe attempts one move in that direction.",
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

            OutlinedTextField(
                value = seedText,
                onValueChange = { value ->
                    seedText = value.filter { it == '-' || it.isDigit() }
                },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Maze seed") },
                supportingText = { Text("Use the same size and seed to reproduce this maze.") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                trailingIcon = {
                    Button(
                        onClick = {
                            seedText.toLongOrNull()?.let { seed = it }
                        },
                        enabled = seedText.toLongOrNull() != null,
                    ) {
                        Text("Generate")
                    }
                },
            )

            Text(
                text = "Seed: $seed  •  Moves: ${playState.moveCount}",
                style = MaterialTheme.typography.labelLarge,
            )

            SwipeableMazeBoard(
                playState = playState,
                onMove = { direction ->
                    if (!playState.completed) {
                        playState = playState.move(direction)
                    }
                },
            )

            if (playState.completed) {
                Text(
                    text = "Maze solved in ${playState.moveCount} moves!",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary,
                )
            } else {
                Text(
                    text = "Swipe up, down, left, or right on the maze to move.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                OutlinedButton(onClick = { playState = playState.reset() }) {
                    Text("Restart")
                }
                Button(
                    onClick = {
                        seed += 1L
                        seedText = seed.toString()
                    },
                ) {
                    Text("New maze")
                }
            }

            Text(
                text = "Start = tertiary marker • Goal = secondary marker • Player = primary marker",
                style = MaterialTheme.typography.bodySmall,
            )
            Text(
                text = "Tip: the seed makes a maze reproducible. The same size and seed always produce the same topology.",
                style = MaterialTheme.typography.bodySmall,
            )
            Spacer(Modifier.height(8.dp))
        }
    }
}

@Composable
private fun SwipeableMazeBoard(
    playState: MazePlayState,
    onMove: (Direction) -> Unit,
) {
    var dragOffset by remember(playState.maze) { mutableStateOf(Offset.Zero) }
    val swipeThreshold = 36f

    MazeBoard(
        maze = playState.maze,
        player = playState.player,
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .pointerInput(playState.maze, playState.completed) {
                detectDragGestures(
                    onDragStart = {
                        dragOffset = Offset.Zero
                    },
                    onDrag = { change, dragAmount ->
                        change.consume()
                        dragOffset += dragAmount
                    },
                    onDragEnd = {
                        val horizontal = abs(dragOffset.x) > abs(dragOffset.y)
                        val direction = when {
                            dragOffset.getDistance() < swipeThreshold -> null
                            horizontal && dragOffset.x > 0f -> Direction.EAST
                            horizontal && dragOffset.x < 0f -> Direction.WEST
                            !horizontal && dragOffset.y > 0f -> Direction.SOUTH
                            !horizontal && dragOffset.y < 0f -> Direction.NORTH
                            else -> null
                        }

                        direction?.let(onMove)
                        dragOffset = Offset.Zero
                    },
                    onDragCancel = {
                        dragOffset = Offset.Zero
                    },
                )
            },
    )
}
