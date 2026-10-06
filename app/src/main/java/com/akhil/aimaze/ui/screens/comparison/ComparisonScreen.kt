package com.akhil.aimaze.ui.screens.comparison

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.akhil.aimaze.data.history.AiMazeDatabase
import com.akhil.aimaze.data.history.BenchmarkHistoryRepository
import com.akhil.aimaze.domain.benchmark.BenchmarkReport
import com.akhil.aimaze.domain.benchmark.BenchmarkSuite
import com.akhil.aimaze.domain.maze.generation.DepthFirstMazeGenerator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun ComparisonScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val mazeSeed = 42L
    val maze = remember { DepthFirstMazeGenerator.generate(12, 12, seed = mazeSeed) }
    val context = LocalContext.current
    val historyRepository = remember(context) {
        BenchmarkHistoryRepository(AiMazeDatabase.get(context).benchmarkHistoryDao())
    }
    val scope = rememberCoroutineScope()
    var report by remember { mutableStateOf<BenchmarkReport?>(null) }
    var running by remember { mutableStateOf(false) }

    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background,
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedButton(onClick = onBack, shape = RoundedCornerShape(16.dp)) {
                    Text("‹")
                }
                Column {
                    Text(
                        "Race Mode",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Black,
                    )
                    Text(
                        "Same maze. Different solvers. Clean scoreboards.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                ),
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    Text(
                        "ARENA",
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Black,
                    )
                    Text(
                        "12×12 • Level #42",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Black,
                    )
                    Text(
                        "Every solver gets the exact same maze.",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }

            Button(
                enabled = !running,
                onClick = {
                    running = true
                    report = null
                    scope.launch {
                        val completed = withContext(Dispatchers.Default) {
                            BenchmarkSuite.run(
                                maze = maze,
                                qLearningEpisodes = 600,
                                randomTrials = 50,
                            )
                        }
                        report = completed
                        historyRepository.save(
                            report = completed,
                            rows = maze.rows,
                            columns = maze.columns,
                            mazeSeed = mazeSeed,
                        )
                        running = false
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
            ) {
                Text(
                    if (running) "RACE IN PROGRESS" else "START RACE",
                    fontWeight = FontWeight.Black,
                )
            }

            if (running) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            }

            report?.let { data ->
                Text(
                    "SCOREBOARD",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Black,
                )

                data.deterministicSearches.forEach { item ->
                    ScoreCard(
                        title = item.name,
                        badge = "SEARCH",
                        lines = listOf(
                            "Path: ${item.pathLength}",
                            "Explored: ${item.nodesExplored}",
                            "Time: %.3f ms".format(item.executionNanos / 1_000_000.0),
                        ),
                    )
                }

                ScoreCard(
                    title = data.reinforcementLearning.name,
                    badge = "TRAINED",
                    lines = listOf(
                        "Rounds: ${data.reinforcementLearning.trainingEpisodes}",
                        "Success: ${(data.reinforcementLearning.successRate * 100).toInt()}%",
                        "Best path: ${data.reinforcementLearning.learnedPathLength ?: "—"}",
                    ),
                )

                ScoreCard(
                    title = data.stochasticBaseline.name,
                    badge = "RANDOM",
                    lines = listOf(
                        "Trials: ${data.stochasticBaseline.trials}",
                        "Success: ${(data.stochasticBaseline.successRate * 100).toInt()}%",
                        "Best path: ${data.stochasticBaseline.bestSuccessfulPathLength ?: "—"}",
                    ),
                )

                Text(
                    "Search solvers and trained/random solvers use different score types, so the app keeps their metrics separate instead of forcing a fake single winner.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

@Composable
private fun ScoreCard(
    title: String,
    badge: String,
    lines: List<String>,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    title,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Black,
                )
                Text(
                    badge,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                )
            }
            lines.forEach {
                Text(
                    it,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}
