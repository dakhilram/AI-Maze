package com.akhil.aimaze.ui.screens.comparison

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
import com.akhil.aimaze.data.history.RaceRecordRepository
import com.akhil.aimaze.domain.maze.Direction
import com.akhil.aimaze.domain.maze.Position
import com.akhil.aimaze.domain.maze.generation.DepthFirstMazeGenerator
import com.akhil.aimaze.domain.pathfinding.AStarPathfinder
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
fun ComparisonScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val raceRepository = remember(context) {
        RaceRecordRepository(AiMazeDatabase.get(context).raceRecordDao())
    }

    var mazeSize by remember { mutableIntStateOf(8) }
    var mazeNumber by remember { mutableIntStateOf(1) }
    val mazeSeed = remember(mazeSize, mazeNumber) {
        70_000L + mazeSize * 997L + mazeNumber * 7_919L
    }
    var raceStarted by remember(mazeSize, mazeSeed) { mutableStateOf(false) }
    var winner by remember(mazeSize, mazeSeed) { mutableStateOf<String?>(null) }
    var botIndex by remember(mazeSize, mazeSeed) { mutableIntStateOf(0) }
    var countdown by remember(mazeSize, mazeSeed) { mutableIntStateOf(0) }
    var startToken by remember { mutableIntStateOf(0) }
    val feedback = rememberGameFeedback()

    val maze = remember(mazeSize, mazeSeed) {
        DepthFirstMazeGenerator.generate(mazeSize, mazeSize, seed = mazeSeed)
    }
    val botPath = remember(maze) { AStarPathfinder.solve(maze).path }
    var playState by remember(maze) {
        mutableStateOf(MazePlayState.initial(maze))
    }

    LaunchedEffect(startToken) {
        if (startToken == 0) return@LaunchedEffect
        playState = MazePlayState.initial(maze)
        botIndex = 0
        winner = null
        for (value in 3 downTo 1) {
            countdown = value
            feedback.countdown()
            delay(650)
        }
        countdown = 0
        feedback.start()
        raceStarted = true
    }

    LaunchedEffect(winner) {
        val raceWinner = winner ?: return@LaunchedEffect
        if (raceWinner == "PLAYER") {
            feedback.win()
        } else {
            feedback.lose()
        }
        raceRepository.save(
            rows = maze.rows,
            columns = maze.columns,
            mazeSeed = mazeSeed,
            winner = raceWinner,
            playerMoves = playState.moveCount,
            botSteps = botIndex,
        )
    }

    LaunchedEffect(raceStarted, winner, botPath) {
        if (!raceStarted || winner != null || botPath.isEmpty()) return@LaunchedEffect

        while (raceStarted && winner == null && botIndex < botPath.lastIndex) {
            delay(botDelayMillis(mazeSize))
            if (winner != null) break
            botIndex += 1
            if (botIndex >= botPath.lastIndex && winner == null) {
                winner = "BOT"
                raceStarted = false
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
                        "Beat the Bot",
                        color = Color.White,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Black,
                    )
                    Text(
                        "Same maze. Same start. First to the exit wins.",
                        color = Color.White.copy(alpha = 0.62f),
                    )
                }
            }

            DifficultySelector(
                selectedSize = mazeSize,
                enabled = !raceStarted && countdown == 0,
                onSizeSelected = {
                    mazeSize = it
                    mazeNumber = 1
                    winner = null
                    raceStarted = false
                    botIndex = 0
                },
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                GameStatTile("YOU", playState.moveCount.toString(), Modifier.weight(1f))
                GameStatTile("BOT", botIndex.toString(), Modifier.weight(1f))
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
                    SwipeRaceBoard(
                        playState = playState,
                        botPosition = botPath.getOrNull(botIndex),
                        enabled = raceStarted && winner == null,
                        onMove = { direction ->
                            val before = playState
                            val moved = before.move(direction)
                            if (moved === before) {
                                feedback.blocked()
                            } else {
                                feedback.move()
                                playState = moved
                                if (moved.completed && winner == null) {
                                    winner = "PLAYER"
                                    raceStarted = false
                                }
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
                visible = winner != null,
                enter = fadeIn() + scaleIn(),
            ) {
                val playerWon = winner == "PLAYER"
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (playerWon) {
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.16f)
                        } else {
                            Color(0x33FF4D4D)
                        },
                    ),
                    border = BorderStroke(
                        1.dp,
                        if (playerWon) {
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.36f)
                        } else {
                            Color(0x55FF6868)
                        },
                    ),
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(5.dp),
                    ) {
                        Text(
                            if (playerWon) "YOU WIN" else "BOT WINS",
                            color = if (playerWon) {
                                MaterialTheme.colorScheme.primary
                            } else {
                                Color(0xFFFF7A7A)
                            },
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Black,
                        )
                        Text(
                            if (playerWon) {
                                "Clean run. You reached the exit first."
                            } else {
                                "The bot got there first. Cut wasted moves and race again."
                            },
                            color = Color.White,
                        )
                    }
                }
            }

            if (winner == null) {
                Text(
                    if (raceStarted) {
                        "GO! Orange is you. Blue is the bot."
                    } else {
                        "Pick a difficulty and start the race when you are ready."
                    },
                    color = Color.White.copy(alpha = 0.66f),
                )
            }

            if ((!raceStarted || winner != null) && countdown == 0) {
                Button(
                    onClick = {
                        feedback.button()
                        raceStarted = false
                        startToken += 1
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                ) {
                    Text(
                        if (winner == null) "START RACE" else "RACE AGAIN",
                        fontWeight = FontWeight.Black,
                    )
                }
            }

            if (winner != null) {
                OutlinedButton(
                    onClick = {
                        feedback.button()
                        mazeNumber += 1
                        raceStarted = false
                        winner = null
                        botIndex = 0
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
            active = winner == "PLAYER",
            modifier = Modifier.fillMaxSize(),
        )
    }
}

@Composable
private fun SwipeRaceBoard(
    playState: MazePlayState,
    botPosition: Position?,
    enabled: Boolean,
    onMove: (Direction) -> Unit,
) {
    var dragOffset by remember(playState.maze) { mutableStateOf(Offset.Zero) }

    MazeBoard(
        maze = playState.maze,
        player = playState.player,
        opponent = botPosition,
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

private fun botDelayMillis(size: Int): Long = when (size) {
    8 -> 520L
    12 -> 470L
    16 -> 430L
    else -> 390L
}
