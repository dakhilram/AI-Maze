package com.akhil.aimaze.ui.screens.daily

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.akhil.aimaze.data.history.AiMazeDatabase
import com.akhil.aimaze.data.history.SoloRunRecordRepository
import com.akhil.aimaze.domain.maze.Direction
import com.akhil.aimaze.domain.maze.generation.DepthFirstMazeGenerator
import com.akhil.aimaze.domain.pathfinding.AStarPathfinder
import com.akhil.aimaze.domain.play.MazePlayState
import com.akhil.aimaze.ui.components.MazeBoard
import com.akhil.aimaze.ui.game.GameBackdrop
import com.akhil.aimaze.ui.game.GameBackdropStyle
import com.akhil.aimaze.ui.game.GameBackButton
import com.akhil.aimaze.ui.game.GameStatTile
import com.akhil.aimaze.ui.game.WinBurst
import com.akhil.aimaze.ui.game.rememberGameFeedback
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.abs

@Composable
fun DailyChallengeScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val repository = remember(context) {
        SoloRunRecordRepository(AiMazeDatabase.get(context).soloRunRecordDao())
    }
    val today = remember { Calendar.getInstance() }
    val dailySeed = remember(today) {
        today.get(Calendar.YEAR).toLong() * 1000L + today.get(Calendar.DAY_OF_YEAR)
    }
    val dateLabel = remember(today) {
        SimpleDateFormat("MMM d", Locale.getDefault()).format(today.time)
    }
    val maze = remember(dailySeed) {
        DepthFirstMazeGenerator.generate(12, 12, seed = dailySeed)
    }
    val par = remember(maze) { AStarPathfinder.solve(maze).pathLength }

    var playState by remember(maze) { mutableStateOf(MazePlayState.initial(maze)) }
    var elapsedMs by remember(maze) { mutableLongStateOf(0L) }
    var running by remember(maze) { mutableStateOf(false) }
    var saved by remember(maze) { mutableStateOf(false) }
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
            if (!saved) {
                repository.save(
                    mode = "DAILY",
                    rows = maze.rows,
                    columns = maze.columns,
                    mazeSeed = dailySeed,
                    moves = playState.moveCount,
                    elapsedMs = elapsedMs,
                    stars = dailyStars(playState.moveCount, par, elapsedMs),
                )
                saved = true
            }
        }
    }

    GameBackdrop(
        style = GameBackdropStyle.Gameplay,
        modifier = modifier,
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 18.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                GameBackButton(
                    onClick = {
                        feedback.button()
                        onBack()
                    },
                )
                Column {
                    Text(
                        "Daily Challenge",
                        color = Color.White,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Black,
                    )
                    Text(
                        "$dateLabel • same maze for the whole day",
                        color = Color.White.copy(alpha = 0.62f),
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                GameStatTile("TIME", formatDailyTime(elapsedMs), Modifier.weight(1f))
                GameStatTile("MOVES", playState.moveCount.toString(), Modifier.weight(1f))
                GameStatTile("TARGET", par.toString(), Modifier.weight(1f))
            }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .graphicsLayer { translationX = shake.value },
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                ),
            ) {
                Box(Modifier.padding(12.dp)) {
                    DailySwipeBoard(
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
                val stars = dailyStars(playState.moveCount, par, elapsedMs)
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(22.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                    ),
                ) {
                    Column(
                        modifier = Modifier.padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp),
                    ) {
                        Text(
                            "DAILY CLEARED",
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Black,
                        )
                        Text(
                            "★".repeat(stars) + "☆".repeat(3 - stars),
                            style = MaterialTheme.typography.displaySmall,
                            color = MaterialTheme.colorScheme.primary,
                        )
                        Text(
                            "${playState.moveCount} moves • ${formatDailyTime(elapsedMs)}",
                            color = Color.White,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                }
            }

            if (!playState.completed) {
                Text(
                    "Everyone gets today's same maze. Match the target route and finish fast.",
                    color = Color.White.copy(alpha = 0.66f),
                )
            }

            Button(
                onClick = {
                    playState = MazePlayState.initial(maze)
                    elapsedMs = 0L
                    running = false
                    saved = false
                    feedback.button()
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
            ) {
                Text(
                    if (playState.completed) "REPLAY TODAY" else "RESTART",
                    fontWeight = FontWeight.Black,
                )
            }
        }

        WinBurst(
            active = playState.completed,
            modifier = Modifier.fillMaxSize(),
        )
    }
}

@Composable
private fun DailySwipeBoard(
    playState: MazePlayState,
    onMove: (Direction) -> Unit,
) {
    var dragOffset by remember(playState.maze) { mutableStateOf(Offset.Zero) }

    MazeBoard(
        maze = playState.maze,
        player = playState.player,
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .pointerInput(playState.maze, playState.completed) {
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

private fun dailyStars(moves: Int, par: Int, elapsedMs: Long): Int {
    val moveRatio = moves.toDouble() / par.coerceAtLeast(1)
    val seconds = elapsedMs / 1000.0
    return when {
        moveRatio <= 1.05 && seconds <= 45.0 -> 3
        moveRatio <= 1.25 && seconds <= 75.0 -> 2
        else -> 1
    }
}

private fun formatDailyTime(ms: Long): String {
    val seconds = ms / 1000
    val minutes = seconds / 60
    val remaining = seconds % 60
    val tenths = (ms % 1000) / 100
    return "%d:%02d.%d".format(minutes, remaining, tenths)
}
