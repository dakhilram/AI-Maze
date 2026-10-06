package com.akhil.aimaze.ui.screens.timeattack

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
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
import com.akhil.aimaze.ui.game.DifficultySelector
import com.akhil.aimaze.ui.game.GameBackdrop
import com.akhil.aimaze.ui.game.GameBackdropStyle
import com.akhil.aimaze.ui.game.GameBackButton
import com.akhil.aimaze.ui.game.GameStatTile
import com.akhil.aimaze.ui.game.WinBurst
import com.akhil.aimaze.ui.game.rememberGameFeedback
import kotlinx.coroutines.delay
import kotlin.math.abs

@Composable
fun TimeAttackScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val soloRepository = remember(context) {
        SoloRunRecordRepository(AiMazeDatabase.get(context).soloRunRecordDao())
    }

    var mazeSize by remember { mutableIntStateOf(8) }
    var mazeNumber by remember { mutableIntStateOf(1) }
    val mazeSeed = remember(mazeSize, mazeNumber) {
        40_000L + mazeSize * 1_003L + mazeNumber * 7_919L
    }
    var playState by remember(mazeSize, mazeSeed) {
        mutableStateOf(
            MazePlayState.initial(
                DepthFirstMazeGenerator.generate(mazeSize, mazeSize, seed = mazeSeed),
            ),
        )
    }
    var remainingSeconds by remember(mazeSize, mazeSeed) {
        mutableIntStateOf(timeLimitFor(mazeSize))
    }
    var running by remember(mazeSize, mazeSeed) { mutableStateOf(false) }
    var countdown by remember(mazeSize, mazeSeed) { mutableIntStateOf(0) }
    var startToken by remember { mutableIntStateOf(0) }
    var lost by remember(mazeSize, mazeSeed) { mutableStateOf(false) }
    var saved by remember(mazeSize, mazeSeed) { mutableStateOf(false) }
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
                    mazeSeed = mazeSeed,
                    moves = playState.moveCount,
                    elapsedMs = (limit - remainingSeconds) * 1_000L,
                    stars = stars,
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
                Column {
                    Text(
                        "Time Attack",
                        color = Color.White,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Black,
                    )
                    Text(
                        "Escape before the clock hits zero.",
                        color = Color.White.copy(alpha = 0.62f),
                    )
                }
            }

            DifficultySelector(
                selectedSize = mazeSize,
                enabled = !running && countdown == 0,
                onSizeSelected = {
                    mazeSize = it
                    mazeNumber = 1
                },
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                GameStatTile("TIME", "${remainingSeconds}s", Modifier.weight(1f))
                GameStatTile("MOVES", playState.moveCount.toString(), Modifier.weight(1f))
                GameStatTile("MAZE", "#$mazeNumber", Modifier.weight(1f))
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
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
                    SwipeBoard(
                        playState = playState,
                        enabled = running && !lost && !playState.completed,
                        onMove = { direction ->
                            val before = playState
                            val after = before.move(direction)
                            if (after === before) {
                                feedback.blocked()
                            } else {
                                feedback.move()
                                playState = after
                            }
                        },
                    )

                    if (countdown > 0) {
                        Box(
                            modifier = Modifier.matchParentSize(),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(
                                countdown.toString(),
                                color = MaterialTheme.colorScheme.primary,
                                style = MaterialTheme.typography.displayLarge,
                                fontWeight = FontWeight.Black,
                            )
                        }
                    }
                }
            }

            AnimatedVisibility(
                visible = playState.completed || lost,
                enter = fadeIn() + scaleIn(),
            ) {
                val title = if (playState.completed) "TIME CLEARED" else "TIME'S UP"
                val subtitle = if (playState.completed) {
                    "${remainingSeconds}s left • ${playState.moveCount} moves"
                } else {
                    "Retry this maze or load another one."
                }
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (playState.completed) {
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.16f)
                        } else {
                            Color(0x33FF4D4D)
                        },
                    ),
                    border = BorderStroke(
                        1.dp,
                        if (playState.completed) {
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.36f)
                        } else {
                            Color(0x55FF6868)
                        },
                    ),
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                    ) {
                        Text(
                            title,
                            color = if (playState.completed) {
                                MaterialTheme.colorScheme.primary
                            } else {
                                Color(0xFFFF7A7A)
                            },
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Black,
                        )
                        Text(
                            subtitle,
                            color = Color.White,
                        )
                    }
                }
            }

            if (!running && !playState.completed && !lost) {
                Text(
                    "Pick a difficulty, then start when you are ready.",
                    color = Color.White.copy(alpha = 0.66f),
                )
            } else if (running) {
                Text(
                    "Keep moving — every second counts.",
                    color = Color.White.copy(alpha = 0.66f),
                )
            }

            if (!running && countdown == 0) {
                Button(
                    onClick = {
                        feedback.button()
                        startToken += 1
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                ) {
                    Text(
                        if (playState.completed || lost) "RETRY MAZE" else "START",
                        fontWeight = FontWeight.Black,
                    )
                }

                OutlinedButton(
                    onClick = {
                        feedback.button()
                        mazeNumber += 1
                        lost = false
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    border = BorderStroke(
                        width = 1.dp,
                        color = Color.White.copy(alpha = 0.26f),
                    ),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color.White,
                    ),
                ) {
                    Text("NEW MAZE", fontWeight = FontWeight.Black)
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
private fun SwipeBoard(
    playState: MazePlayState,
    enabled: Boolean,
    onMove: (Direction) -> Unit,
) {
    var dragOffset by remember(playState.maze) { mutableStateOf(Offset.Zero) }

    MazeBoard(
        maze = playState.maze,
        player = playState.player,
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .pointerInput(playState.maze, enabled) {
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
