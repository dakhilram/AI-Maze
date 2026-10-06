package com.akhil.aimaze.ui.screens.comparison

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
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
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
    var levelSeed by remember { mutableLongStateOf(77L) }
    var raceStarted by remember(mazeSize, levelSeed) { mutableStateOf(false) }
    var winner by remember(mazeSize, levelSeed) { mutableStateOf<String?>(null) }
    var botIndex by remember(mazeSize, levelSeed) { mutableIntStateOf(0) }

    val maze = remember(mazeSize, levelSeed) {
        DepthFirstMazeGenerator.generate(mazeSize, mazeSize, seed = levelSeed)
    }
    val botSearch = remember(maze) { AStarPathfinder.solve(maze) }
    val botPath = botSearch.path

    var playState by remember(maze) {
        mutableStateOf(MazePlayState.initial(maze))
    }

    LaunchedEffect(winner) {
        val raceWinner = winner ?: return@LaunchedEffect
        raceRepository.save(
            rows = maze.rows,
            columns = maze.columns,
            mazeSeed = levelSeed,
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
            }
        }
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
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedButton(
                    onClick = onBack,
                    shape = RoundedCornerShape(16.dp),
                ) {
                    Text("‹")
                }
                Column {
                    Text(
                        "Beat the Bot",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Black,
                    )
                    Text(
                        "You and the bot race the exact same maze.",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }

            DifficultyPicker(
                selectedSize = mazeSize,
                onSizeSelected = {
                    mazeSize = it
                    raceStarted = false
                    winner = null
                    botIndex = 0
                },
            )

            RaceHud(
                playerMoves = playState.moveCount,
                botSteps = botIndex,
                levelSeed = levelSeed,
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                ),
            ) {
                Box(modifier = Modifier.padding(12.dp)) {
                    SwipeRaceBoard(
                        playState = playState,
                        botPosition = botPath.getOrNull(botIndex),
                        enabled = raceStarted && winner == null,
                        onMove = { direction ->
                            val moved = playState.move(direction)
                            playState = moved
                            if (moved.completed && winner == null) {
                                winner = "PLAYER"
                            }
                        },
                    )
                }
            }

            when (winner) {
                "PLAYER" -> ResultCard(
                    title = "YOU WIN",
                    subtitle = "You escaped before the optimal-route bot.",
                )
                "BOT" -> ResultCard(
                    title = "BOT WINS",
                    subtitle = "Try again and cut unnecessary moves.",
                )
                else -> {
                    Text(
                        if (raceStarted) {
                            "GO! Swipe through the maze before the blue bot reaches the exit."
                        } else {
                            "Orange is you. Blue is the bot. Tap START RACE when you are ready."
                        },
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }

            if (!raceStarted || winner != null) {
                Button(
                    onClick = {
                        playState = MazePlayState.initial(maze)
                        botIndex = 0
                        winner = null
                        raceStarted = true
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
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    OutlinedButton(
                        onClick = {
                            levelSeed += 1L
                            raceStarted = false
                            winner = null
                            botIndex = 0
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(18.dp),
                    ) {
                        Text("NEXT LEVEL", fontWeight = FontWeight.Bold)
                    }
                    OutlinedButton(
                        onClick = {
                            playState = MazePlayState.initial(maze)
                            botIndex = 0
                            winner = null
                            raceStarted = false
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(18.dp),
                    ) {
                        Text("RESET", fontWeight = FontWeight.Bold)
                    }
                }

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                    ) {
                        Text(
                            "UNDER THE HOOD",
                            color = MaterialTheme.colorScheme.primary,
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Black,
                        )
                        Text(
                            "The bot uses A* search and follows an optimal route of ${botSearch.pathLength} moves.",
                        )
                        Text(
                            "A* explored ${botSearch.nodesExplored} cells to solve this level.",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            }
        }
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
            androidx.compose.material3.FilterChip(
                selected = selectedSize == size,
                onClick = { onSizeSelected(size) },
                label = { Text(label, fontWeight = FontWeight.Bold) },
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
private fun RaceHud(
    playerMoves: Int,
    botSteps: Int,
    levelSeed: Long,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        HudTile("YOU", playerMoves.toString(), Modifier.weight(1f))
        HudTile("BOT", botSteps.toString(), Modifier.weight(1f))
        HudTile("LEVEL", "#$levelSeed", Modifier.weight(1f))
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
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.Bold,
            )
            Text(
                value,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Black,
            )
        }
    }
}

@Composable
private fun ResultCard(
    title: String,
    subtitle: String,
) {
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
                title,
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Black,
            )
            Text(subtitle)
        }
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
    val swipeThreshold = 36f

    MazeBoard(
        maze = playState.maze,
        player = playState.player,
        opponent = botPosition,
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .pointerInput(playState.maze, enabled) {
                detectDragGestures(
                    onDragStart = {
                        dragOffset = Offset.Zero
                    },
                    onDrag = { change, dragAmount ->
                        if (!enabled) return@detectDragGestures
                        change.consume()
                        dragOffset += dragAmount
                    },
                    onDragEnd = {
                        if (!enabled) {
                            dragOffset = Offset.Zero
                            return@detectDragGestures
                        }

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

private fun botDelayMillis(size: Int): Long = when (size) {
    8 -> 460L
    12 -> 420L
    16 -> 380L
    else -> 340L
}
