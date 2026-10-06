package com.akhil.aimaze.ui.screens.comparison

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
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
    val maze = remember { DepthFirstMazeGenerator.generate(12, 12, seed = 42L) }
    val scope = rememberCoroutineScope()
    var report by remember { mutableStateOf<BenchmarkReport?>(null) }
    var running by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        OutlinedButton(onClick = onBack) { Text("Back") }
        Text("AI vs Algorithms", style = MaterialTheme.typography.headlineMedium)
        Text(
            "All methods use the exact same 12×12 maze (seed 42), but their metrics are intentionally kept in separate families.",
            style = MaterialTheme.typography.bodyLarge,
        )

        Button(
            enabled = !running,
            onClick = {
                running = true
                report = null
                scope.launch {
                    report = withContext(Dispatchers.Default) {
                        BenchmarkSuite.run(maze = maze, qLearningEpisodes = 600, randomTrials = 50)
                    }
                    running = false
                }
            },
        ) { Text("Run benchmark") }

        if (running) {
            LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            Text("Running locally…")
        }

        report?.let { data ->
            Text("Deterministic shortest-path search", style = MaterialTheme.typography.titleLarge)
            Text(
                "A* and Dijkstra can be directly compared on optimal path length, nodes explored, and one-shot search time.",
                style = MaterialTheme.typography.bodySmall,
            )
            data.deterministicSearches.forEach { item ->
                BenchmarkCard(
                    title = item.name,
                    lines = listOf(
                        "Path length: ${item.pathLength}",
                        "Nodes explored: ${item.nodesExplored}",
                        "Execution: ${item.executionNanos / 1_000_000.0} ms",
                    ),
                )
            }

            Text("Reinforcement learning", style = MaterialTheme.typography.titleLarge)
            BenchmarkCard(
                title = data.reinforcementLearning.name,
                lines = listOf(
                    "Training episodes: ${data.reinforcementLearning.trainingEpisodes}",
                    "Training success rate: ${(data.reinforcementLearning.successRate * 100).toInt()}%",
                    "Learned greedy path: ${data.reinforcementLearning.learnedPathLength ?: "not learned"}",
                    "Final exploration ε: %.3f".format(data.reinforcementLearning.finalEpsilon),
                    "Training time: ${data.reinforcementLearning.trainingNanos / 1_000_000.0} ms",
                ),
            )

            Text("Stochastic baseline", style = MaterialTheme.typography.titleLarge)
            BenchmarkCard(
                title = data.stochasticBaseline.name,
                lines = listOf(
                    "Trials: ${data.stochasticBaseline.trials}",
                    "Success rate: ${(data.stochasticBaseline.successRate * 100).toInt()}%",
                    "Average successful steps: ${data.stochasticBaseline.averageSuccessfulSteps?.let { "%.1f".format(it) } ?: "n/a"}",
                    "Best successful path: ${data.stochasticBaseline.bestSuccessfulPathLength ?: "n/a"}",
                ),
            )

            Text(
                "Do not compare Q-Learning training time with A*/Dijkstra search time as if they measure the same task. Q-Learning pays a training cost to learn a policy; deterministic searches solve a specific maze directly.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary,
            )
        }
    }
}

@Composable
private fun BenchmarkCard(title: String, lines: List<String>) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Text(title, style = MaterialTheme.typography.titleMedium)
            lines.forEach { Text(it, style = MaterialTheme.typography.bodyMedium) }
        }
    }
}
