package com.akhil.aimaze.ui.screens.play

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.unit.dp
import com.akhil.aimaze.domain.maze.Direction
import com.akhil.aimaze.domain.maze.generation.DepthFirstMazeGenerator
import com.akhil.aimaze.domain.play.MazePlayState
import com.akhil.aimaze.ui.components.MazeBoard

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

