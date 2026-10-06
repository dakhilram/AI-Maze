@file:OptIn(ExperimentalMaterial3Api::class)

package com.akhil.aimaze.ui.screens.play

import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.akhil.aimaze.domain.maze.Direction
import com.akhil.aimaze.domain.maze.generation.DepthFirstMazeGenerator
import com.akhil.aimaze.domain.pathfinding.AStarPathfinder
import com.akhil.aimaze.domain.play.MazePlayState
import com.akhil.aimaze.ui.components.MazeBoard
import kotlin.math.abs

@Composable
fun PlayMazeScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var mazeSize by remember { mutableIntStateOf(8) }
    var levelSeed by remember { mutableLongStateOf(42L) }
    var hintsUsed by remember(mazeSize, levelSeed) { mutableIntStateOf(0) }
    var hintPath by remember(mazeSize, levelSeed) { mutableStateOf(emptyList<com.akhil.aimaze.domain.maze.Position>()) }
    var playState by remember(mazeSize, levelSeed) {
        mutableStateOf(
            MazePlayState.initial(
                DepthFirstMazeGenerator.generate(
                    rows = mazeSize,
                    columns = mazeSize,
                    seed = levelSeed,
                ),
            ),
        )
    }

    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background,
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 18.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            GameTopBar(
                title = "Maze Run",
                onBack = onBack,
            )

            DifficultyPicker(
                selectedSize = mazeSize,
                onSizeSelected = { mazeSize = it },
            )

            RunHud(
                moves = playState.moveCount,
                levelSeed = levelSeed,
                completed = playState.completed,
                hintsUsed = hintsUsed,
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                ),
            ) {
                Box(modifier = Modifier.padding(12.dp)) {
                    SwipeableMazeBoard(
                        playState = playState,
                        hintPath = hintPath,
                        onMove = { direction ->
                            if (!playState.completed) {
                                playState = playState.move(direction)
                                hintPath = emptyList()
                            }
                        },
                    )
                }
            }

            if (playState.completed) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.14f),
                    ),
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                    ) {
                        Text(
                            text = "MAZE CLEARED",
                            color = MaterialTheme.colorScheme.primary,
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Black,
                        )
                        Text(
                            text = "${playState.moveCount} moves",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Black,
                        )
                    }
                }
            } else {
                Text(
                    text = "Swipe anywhere on the maze to move one cell.",
                    modifier = Modifier.fillMaxWidth(),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodyMedium,
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                OutlinedButton(
                    onClick = {
                        playState = playState.reset()
                        hintPath = emptyList()
                        hintsUsed = 0
                    },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(18.dp),
                ) {
                    Text("RESTART", fontWeight = FontWeight.Bold)
                }

                OutlinedButton(
                    onClick = {
                        val route = AStarPathfinder.solve(
                            maze = playState.maze,
                            start = playState.player,
                            goal = playState.maze.goal,
                        ).path
                        hintPath = route.take(2)
                        if (hintPath.size > 1) hintsUsed += 1
                    },
                    enabled = !playState.completed,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(18.dp),
                ) {
                    Text("HINT", fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = {
                        levelSeed += 1L
                        hintPath = emptyList()
                        hintsUsed = 0
                    },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(18.dp),
                ) {
                    Text(
                        text = if (playState.completed) "NEXT" else "NEW",
                        fontWeight = FontWeight.Black,
                    )
                }
            }
        }
    }
}

@Composable
private fun GameTopBar(
    title: String,
    onBack: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        OutlinedButton(
            onClick = onBack,
            shape = RoundedCornerShape(16.dp),
        ) {
            Text("‹")
        }
        Text(
            text = title,
            modifier = Modifier.padding(start = 12.dp),
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Black,
        )
    }
}

@Composable
private fun DifficultyPicker(
    selectedSize: Int,
    onSizeSelected: (Int) -> Unit,
) {
    val options = listOf(
        8 to "EASY",
        12 to "NORMAL",
        16 to "HARD",
        20 to "EXPERT",
    )

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        options.forEach { (size, label) ->
            FilterChip(
                selected = selectedSize == size,
                onClick = { onSizeSelected(size) },
                label = { Text(label, fontWeight = FontWeight.Bold) },
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
private fun RunHud(
    moves: Int,
    levelSeed: Long,
    completed: Boolean,
    hintsUsed: Int,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        HudTile(
            label = "MOVES",
            value = moves.toString(),
            modifier = Modifier.weight(1f),
        )
        HudTile(
            label = "LEVEL",
            value = "#$levelSeed",
            modifier = Modifier.weight(1f),
        )
        HudTile(
            label = "HINTS",
            value = hintsUsed.toString(),
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
private fun HudTile(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
        ) {
            Text(
                text = label,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Black,
            )
        }
    }
}

@Composable
private fun SwipeableMazeBoard(
    playState: MazePlayState,
    hintPath: List<com.akhil.aimaze.domain.maze.Position>,
    onMove: (Direction) -> Unit,
) {
    var dragOffset by remember(playState.maze) { mutableStateOf(Offset.Zero) }
    val swipeThreshold = 36f

    MazeBoard(
        maze = playState.maze,
        player = playState.player,
        path = hintPath,
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
