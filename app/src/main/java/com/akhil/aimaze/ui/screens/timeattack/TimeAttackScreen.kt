package com.akhil.aimaze.ui.screens.timeattack

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.akhil.aimaze.data.history.AiMazeDatabase
import com.akhil.aimaze.data.history.SoloRunRecordRepository
import com.akhil.aimaze.domain.maze.Direction
import com.akhil.aimaze.domain.maze.generation.DepthFirstMazeGenerator
import com.akhil.aimaze.domain.play.MazePlayState
import com.akhil.aimaze.ui.components.MazeBoard
import com.akhil.aimaze.ui.game.rememberGameFeedback
import kotlinx.coroutines.delay
import kotlin.math.abs

@Composable
fun TimeAttackScreen(onBack: () -> Unit, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val soloRepository = remember(context) {
        SoloRunRecordRepository(AiMazeDatabase.get(context).soloRunRecordDao())
    }

    var mazeSize by remember { mutableIntStateOf(8) }
    var levelSeed by remember { mutableLongStateOf(100L) }
    var playState by remember(mazeSize, levelSeed) {
        mutableStateOf(MazePlayState.initial(DepthFirstMazeGenerator.generate(mazeSize, mazeSize, seed = levelSeed)))
    }
    var remainingSeconds by remember(mazeSize, levelSeed) { mutableIntStateOf(timeLimitFor(mazeSize)) }
    var running by remember(mazeSize, levelSeed) { mutableStateOf(false) }
    var countdown by remember(mazeSize, levelSeed) { mutableIntStateOf(0) }
    var startToken by remember { mutableIntStateOf(0) }
    var lost by remember(mazeSize, levelSeed) { mutableStateOf(false) }
    var saved by remember(mazeSize, levelSeed) { mutableStateOf(false) }
    val feedback = rememberGameFeedback()

    LaunchedEffect(startToken) {
        if (startToken == 0) return@LaunchedEffect
        lost = false
        playState = playState.reset()
        remainingSeconds = timeLimitFor(mazeSize)
        for (value in 3 downTo 1) {
            countdown = value
            feedback.countdown()
            delay(650)
        }
        countdown = 0
        feedback.start()
        running = true
    }

    LaunchedEffect(running, playState.completed) {
        if (!running || playState.completed) return@LaunchedEffect
        while (running && !playState.completed && remainingSeconds > 0) {
            delay(1_000)
            remainingSeconds -= 1
        }
        if (remainingSeconds <= 0 && !playState.completed) {
            running = false
            lost = true
            feedback.lose()
        }
    }

    LaunchedEffect(playState.completed) {
        if (playState.completed) {
            running = false
            feedback.win()
            if (!saved) {
                val limit = timeLimitFor(mazeSize)
                val stars = when {
                    remainingSeconds >= (limit * 0.5f).toInt() -> 3
                    remainingSeconds >= (limit * 0.2f).toInt() -> 2
                    else -> 1
                }
                soloRepository.save(
                    mode = "TIME_ATTACK",
                    rows = playState.maze.rows,
                    columns = playState.maze.columns,
                    mazeSeed = levelSeed,
                    moves = playState.moveCount,
                    elapsedMs = (limit - remainingSeconds) * 1_000L,
                    stars = stars,
                )
                saved = true
            }
        }
    }

    Surface(modifier = modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Column(
            modifier = Modifier.fillMaxSize().padding(horizontal = 18.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedButton(onClick = onBack, shape = RoundedCornerShape(16.dp)) { Text("‹") }
                Column {
                    Text("Time Attack", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Black)
                    Text("Find the exit before the clock hits zero.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }

            DifficultyPicker(
                selectedSize = mazeSize,
                enabled = !running && countdown == 0,
                onSizeSelected = { mazeSize = it },
            )

            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                StatTile("TIME", "${remainingSeconds}s", Modifier.weight(1f))
                StatTile("MOVES", playState.moveCount.toString(), Modifier.weight(1f))
                StatTile("LEVEL", "#$levelSeed", Modifier.weight(1f))
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            ) {
                Box(Modifier.padding(12.dp)) {
                    SwipeBoard(
                        playState = playState,
                        enabled = running && !lost && !playState.completed,
                        onMove = { direction ->
                            val before = playState
                            val after = before.move(direction)
                            if (after === before) feedback.blocked()
                            else {
                                feedback.move()
                                playState = after
                            }
                        },
                    )

                    AnimatedVisibility(
                        visible = countdown > 0,
                        enter = fadeIn() + scaleIn(),
                        exit = fadeOut() + scaleOut(),
                        modifier = Modifier.matchParentSize(),
                    ) {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text(
                                countdown.toString(),
                                style = MaterialTheme.typography.displayLarge,
                                fontWeight = FontWeight.Black,
                                color = MaterialTheme.colorScheme.primary,
                            )
                        }
                    }
                }
            }

            when {
                playState.completed -> ResultCard("TIME CLEARED", "${remainingSeconds}s left • ${playState.moveCount} moves")
                lost -> ResultCard("TIME'S UP", "Retry the level or generate a new maze.")
                running -> Text("Keep moving — every swipe counts.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                else -> Text("Choose a difficulty, then hit START.", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }

            if (!running && countdown == 0) {
                Button(
                    onClick = { startToken += 1 },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                ) {
                    Text(if (playState.completed || lost) "RETRY" else "START", fontWeight = FontWeight.Black)
                }

                OutlinedButton(
                    onClick = {
                        levelSeed += 1L
                        lost = false
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                ) {
                    Text("NEW LEVEL", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun DifficultyPicker(selectedSize: Int, enabled: Boolean, onSizeSelected: (Int) -> Unit) {
    val options = listOf(8 to "EASY", 12 to "NORMAL", 16 to "HARD", 20 to "EXPERT")
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        options.forEach { (size, label) ->
            FilterChip(
                selected = selectedSize == size,
                onClick = { onSizeSelected(size) },
                enabled = enabled,
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
private fun ResultCard(title: String, subtitle: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.14f)),
    ) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(title, color = MaterialTheme.colorScheme.primary, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Black)
            Text(subtitle)
        }
    }
}

@Composable
private fun SwipeBoard(playState: MazePlayState, enabled: Boolean, onMove: (Direction) -> Unit) {
    var dragOffset by remember(playState.maze) { mutableStateOf(Offset.Zero) }

    MazeBoard(
        maze = playState.maze,
        player = playState.player,
        modifier = Modifier.fillMaxWidth().aspectRatio(1f).pointerInput(playState.maze, enabled) {
            detectDragGestures(
                onDragStart = { dragOffset = Offset.Zero },
                onDrag = { change, dragAmount ->
                    if (enabled) {
                        change.consume()
                        dragOffset += dragAmount
                    }
                },
                onDragEnd = {
                    if (enabled && dragOffset.getDistance() >= 36f) {
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

private fun timeLimitFor(size: Int): Int = when (size) {
    8 -> 35
    12 -> 60
    16 -> 90
    else -> 120
}
