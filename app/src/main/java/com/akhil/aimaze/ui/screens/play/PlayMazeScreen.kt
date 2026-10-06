package com.akhil.aimaze.ui.screens.play

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
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
import com.akhil.aimaze.ui.game.GamePreferences
import com.akhil.aimaze.ui.game.GameStatTile
import com.akhil.aimaze.ui.game.WinBurst
import com.akhil.aimaze.ui.game.rememberGameFeedback
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.ceil

@Composable
fun PlayMazeScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val preferences = remember(context) { GamePreferences(context) }
    val soloRepository = remember(context) {
        SoloRunRecordRepository(AiMazeDatabase.get(context).soloRunRecordDao())
    }

    var level by remember { mutableStateOf(preferences.campaignLevel) }
    val mazeSize = campaignSize(level)
    val seed = campaignSeed(level)
    val maze = remember(level) {
        DepthFirstMazeGenerator.generate(mazeSize, mazeSize, seed = seed)
    }
    val targetMoves = remember(maze) { AStarPathfinder.solve(maze).pathLength }
    var playState by remember(maze) { mutableStateOf(MazePlayState.initial(maze)) }
    var elapsedMs by remember(maze) { mutableLongStateOf(0L) }
    var running by remember(maze) { mutableStateOf(false) }
    var saved by remember(maze) { mutableStateOf(false) }
    val feedback = rememberGameFeedback()
    val shake = remember { Animatable(0f) }
    val scope = rememberCoroutineScope()

    val pulse = rememberInfiniteTransition(label = "campaignPulse")
    val playGlow by pulse.animateFloat(
        initialValue = 0.96f,
        targetValue = 1.02f,
        animationSpec = infiniteRepeatable(
            animation = tween(900, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "campaignPulseScale",
    )

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
                val stars = starsFor(playState.moveCount, targetMoves)
                soloRepository.save(
                    mode = "MAZE_RUN",
                    rows = maze.rows,
                    columns = maze.columns,
                    mazeSeed = seed,
                    moves = playState.moveCount,
                    elapsedMs = elapsedMs,
                    stars = stars,
                )
                preferences.advanceCampaignFrom(level)
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
                .systemBarsPadding()
                .padding(horizontal = 18.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                GameBackButton(
                    onClick = {
                        feedback.button()
                        onBack()
                    },
                )
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Maze Run",
                        color = Color.White,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Black,
                    )
                    Text(
                        text = "Level $level of ${GamePreferences.CAMPAIGN_LEVELS} • ${campaignTier(level)}",
                        color = Color.White.copy(alpha = 0.62f),
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
            }

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text(
                        text = "CAMPAIGN PROGRESS",
                        color = Color.White.copy(alpha = 0.52f),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Black,
                    )
                    Text(
                        text = "$level / ${GamePreferences.CAMPAIGN_LEVELS}",
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Black,
                    )
                }
                LinearProgressIndicator(
                    progress = { level.toFloat() / GamePreferences.CAMPAIGN_LEVELS },
                    modifier = Modifier.fillMaxWidth(),
                    color = MaterialTheme.colorScheme.primary,
                    trackColor = Color.White.copy(alpha = 0.08f),
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                GameStatTile("TIME", formatTime(elapsedMs), Modifier.weight(1f))
                GameStatTile("MOVES", playState.moveCount.toString(), Modifier.weight(1f))
                GameStatTile("TARGET", targetMoves.toString(), Modifier.weight(1f))
            }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .graphicsLayer { translationX = shake.value },
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xE8212935),
                ),
                border = BorderStroke(
                    width = 1.dp,
                    color = Color.White.copy(alpha = 0.12f),
                ),
            ) {
                Box(modifier = Modifier.padding(12.dp)) {
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
                val stars = starsFor(playState.moveCount, targetMoves)
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(22.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.16f),
                    ),
                    border = BorderStroke(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.38f),
                    ),
                ) {
                    Column(
                        modifier = Modifier.padding(18.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(6.dp),
                    ) {
                        Text(
                            "LEVEL CLEARED",
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Black,
                        )
                        Text(
                            "★".repeat(stars) + "☆".repeat(3 - stars),
                            style = MaterialTheme.typography.displaySmall,
                            color = MaterialTheme.colorScheme.primary,
                        )
                        Text(
                            "${playState.moveCount} moves • target $targetMoves • ${formatTime(elapsedMs)}",
                            color = Color.White,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                        )
                        if (level == GamePreferences.CAMPAIGN_LEVELS) {
                            Text(
                                "You beat all 500 levels. More coming soon.",
                                color = Color.White.copy(alpha = 0.72f),
                            )
                        }
                    }
                }
            }

            if (!playState.completed) {
                Text(
                    text = "Swipe directly on the maze. Match the target route for 3 stars.",
                    color = Color.White.copy(alpha = 0.68f),
                    style = MaterialTheme.typography.bodyMedium,
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                OutlinedButton(
                    onClick = {
                        feedback.button()
                        playState = MazePlayState.initial(maze)
                        elapsedMs = 0L
                        running = false
                        saved = false
                    },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(18.dp),
                    border = BorderStroke(
                        width = 1.dp,
                        color = Color.White.copy(alpha = 0.26f),
                    ),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color.White,
                    ),
                ) {
                    Text("RESTART", fontWeight = FontWeight.Black)
                }

                Button(
                    onClick = {
                        feedback.button()
                        if (playState.completed && level < GamePreferences.CAMPAIGN_LEVELS) {
                            level += 1
                        } else if (!playState.completed) {
                            playState = MazePlayState.initial(maze)
                            elapsedMs = 0L
                            running = false
                            saved = false
                        }
                    },
                    enabled = level < GamePreferences.CAMPAIGN_LEVELS || !playState.completed,
                    modifier = Modifier
                        .weight(1f)
                        .graphicsLayer {
                            if (playState.completed) {
                                scaleX = playGlow
                                scaleY = playGlow
                            }
                        },
                    shape = RoundedCornerShape(18.dp),
                ) {
                    Text(
                        text = when {
                            playState.completed && level < GamePreferences.CAMPAIGN_LEVELS -> "NEXT LEVEL"
                            playState.completed -> "500 CLEARED"
                            else -> "TRY AGAIN"
                        },
                        fontWeight = FontWeight.Black,
                    )
                }
            }
        }

        WinBurst(
            active = playState.completed,
            modifier = Modifier.fillMaxSize(),
        )
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

private fun campaignSize(level: Int): Int = when (level) {
    in 1..125 -> 8
    in 126..250 -> 12
    in 251..375 -> 16
    else -> 20
}

private fun campaignTier(level: Int): String = when (level) {
    in 1..125 -> "EASY"
    in 126..250 -> "MEDIUM"
    in 251..375 -> "HARD"
    else -> "EXPERT"
}

private fun campaignSeed(level: Int): Long = 20_000L + level * 7919L

private fun starsFor(moves: Int, target: Int): Int {
    val twoStarLimit = target + ceil(target * 0.18).toInt().coerceAtLeast(3)
    return when {
        moves <= target -> 3
        moves <= twoStarLimit -> 2
        else -> 1
    }
}

private fun formatTime(ms: Long): String {
    val totalSeconds = ms / 1_000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    val tenths = (ms % 1_000) / 100
    return "%d:%02d.%d".format(minutes, seconds, tenths)
}
