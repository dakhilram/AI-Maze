package com.akhil.aimaze.ui.screens.play

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.akhil.aimaze.domain.maze.Direction
import com.akhil.aimaze.domain.maze.generation.DepthFirstMazeGenerator
import com.akhil.aimaze.domain.pathfinding.AStarPathfinder
import com.akhil.aimaze.domain.play.MazePlayState
import com.akhil.aimaze.ui.components.MazeBoard
import com.akhil.aimaze.ui.game.rememberGameFeedback
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.abs

@Composable
fun PlayMazeScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var mazeSize by remember { mutableIntStateOf(8) }
    var levelSeed by remember { mutableLongStateOf(42L) }
    val maze = remember(mazeSize, levelSeed) {
        DepthFirstMazeGenerator.generate(mazeSize, mazeSize, seed = levelSeed)
    }
    val par = remember(maze) { AStarPathfinder.solve(maze).pathLength }
    var playState by remember(maze) { mutableStateOf(MazePlayState.initial(maze)) }
    var elapsedMs by remember(maze) { mutableLongStateOf(0L) }
    var running by remember(maze) { mutableStateOf(false) }
    val feedback = rememberGameFeedback()
    val shake = remember { Animatable(0f) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(running, playState.completed) {
        while (running && !playState.completed) {
            delay(100)
            elapsedMs += 100
        }
    }

    LaunchedEffect(playState.completed) {
        if (playState.completed) {
            running = false
            feedback.win()
        }
    }

    Surface(modifier = modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Column(
            modifier = Modifier.fillMaxSize().padding(horizontal = 18.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
                OutlinedButton(onClick = onBack, shape = RoundedCornerShape(16.dp)) { Text("‹") }
                Column {
                    Text("Maze Run", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Black)
                    Text("Swipe to the exit. Fewer moves earns more stars.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }

            DifficultyPicker(
                selectedSize = mazeSize,
                onSizeSelected = {
                    mazeSize = it
                    elapsedMs = 0
                    running = false
                },
            )

            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                StatTile("TIME", formatTime(elapsedMs), Modifier.weight(1f))
                StatTile("MOVES", playState.moveCount.toString(), Modifier.weight(1f))
                StatTile("PAR", par.toString(), Modifier.weight(1f))
            }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .graphicsLayer { translationX = shake.value },
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            ) {
                Box(Modifier.padding(12.dp)) {
                    SwipeableMazeBoard(
                        playState = playState,
                        onMove = { direction ->
                            val before = playState
                            val after = before.move(direction)
                            if (after === before) {
                                feedback.blocked()
                                scope.launch {
                                    shake.snapTo(0f)
                                    shake.animateTo(-10f, tween(45))
                                    shake.animateTo(10f, tween(70))
                                    shake.animateTo(0f, tween(55))
                                }
                            } else {
                                if (!running) running = true
                                feedback.move()
                                playState = after
                            }
                        },
                    )
                }
            }

            AnimatedVisibility(
                visible = playState.completed,
                enter = fadeIn() + scaleIn(),
            ) {
                val stars = starsFor(playState.moveCount, par)
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(22.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.14f),
                    ),
                ) {
                    Column(
                        modifier = Modifier.padding(18.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(6.dp),
                    ) {
                        Text("MAZE CLEARED", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Black)
                        Text(
                            "★".repeat(stars) + "☆".repeat(3 - stars),
                            style = MaterialTheme.typography.displaySmall,
                            color = MaterialTheme.colorScheme.primary,
                        )
                        Text(
                            "${playState.moveCount} moves • ${formatTime(elapsedMs)}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                }
            }

            if (!playState.completed) {
                Text(
                    "Swipe up, down, left, or right directly on the maze.",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedButton(
                    onClick = {
                        playState = MazePlayState.initial(maze)
                        elapsedMs = 0
                        running = false
                    },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(18.dp),
                ) {
                    Text("RESTART", fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = {
                        levelSeed += 1L
                        elapsedMs = 0
                        running = false
                    },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(18.dp),
                ) {
                    Text(
                        if (playState.completed) "NEXT MAZE" else "NEW MAZE",
                        fontWeight = FontWeight.Black,
                    )
                }
            }
        }
    }
}

@Composable
private fun DifficultyPicker(selectedSize: Int, onSizeSelected: (Int) -> Unit) {
    val options = listOf(8 to "EASY", 12 to "NORMAL", 16 to "HARD", 20 to "EXPERT")
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
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
private fun StatTile(label: String, value: String, modifier: Modifier = Modifier) {
    Card(modifier = modifier, shape = RoundedCornerShape(16.dp)) {
        Column(Modifier.padding(12.dp)) {
            Text(value, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Black)
            Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun SwipeableMazeBoard(
    playState: MazePlayState,
    onMove: (Direction) -> Unit,
) {
    var dragOffset by remember(playState.maze) { mutableStateOf(Offset.Zero) }

    MazeBoard(
        maze = playState.maze,
        player = playState.player,
        modifier = Modifier.fillMaxWidth().aspectRatio(1f).pointerInput(playState.maze, playState.completed) {
            detectDragGestures(
                onDragStart = { dragOffset = Offset.Zero },
                onDrag = { change, dragAmount ->
                    if (!playState.completed) {
                        change.consume()
                        dragOffset += dragAmount
                    }
                },
                onDragEnd = {
                    if (!playState.completed && dragOffset.getDistance() >= 36f) {
                        val horizontal = abs(dragOffset.x) > abs(dragOffset.y)
                        val direction = when {
                            horizontal && dragOffset.x > 0f -> Direction.EAST
                            horizontal -> Direction.WEST
                            dragOffset.y > 0f -> Direction.SOUTH
                            else -> Direction.NORTH
                        }
                        onMove(direction)
                    }
                    dragOffset = Offset.Zero
                },
                onDragCancel = { dragOffset = Offset.Zero },
            )
        },
    )
}

private fun starsFor(moves: Int, par: Int): Int = when {
    moves <= par -> 3
    moves <= (par * 1.25f).toInt().coerceAtLeast(par + 1) -> 2
    else -> 1
}

private fun formatTime(ms: Long): String {
    val totalSeconds = ms / 1_000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    val tenths = (ms % 1_000) / 100
    return "%d:%02d.%d".format(minutes, seconds, tenths)
}
