package com.akhil.aimaze.ui.screens.training

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.dp
import com.akhil.aimaze.domain.maze.generation.DepthFirstMazeGenerator
import com.akhil.aimaze.domain.rl.EpisodeMetric
import com.akhil.aimaze.domain.rl.QLearningConfig
import com.akhil.aimaze.domain.rl.QLearningResult
import com.akhil.aimaze.domain.rl.QLearningTrainer
import com.akhil.aimaze.ui.components.MazeBoard
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun TrainingScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val maze = remember { DepthFirstMazeGenerator.generate(8, 8, seed = 42L) }
    val scope = rememberCoroutineScope()
    var result by remember { mutableStateOf<QLearningResult?>(null) }
    var isTraining by remember { mutableStateOf(false) }
    var visiblePathSteps by remember { mutableIntStateOf(0) }

    LaunchedEffect(result) {
        visiblePathSteps = 0
        val path = result?.bestPath.orEmpty()
        for (step in path.indices) {
            visiblePathSteps = step + 1
            delay(80)
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        OutlinedButton(onClick = onBack) { Text("Back") }
        Text("Train AI", style = MaterialTheme.typography.headlineMedium)
        Text(
            "Q-Learning learns by trial and error. Early episodes explore heavily; later episodes increasingly exploit learned Q-values.",
            style = MaterialTheme.typography.bodyLarge,
        )

        MazeBoard(
            maze = maze,
            path = result?.bestPath.orEmpty().take(visiblePathSteps),
            player = result?.bestPath?.getOrNull((visiblePathSteps - 1).coerceAtLeast(0)),
            modifier = Modifier.fillMaxWidth().aspectRatio(1f),
        )

        if (isTraining) {
            LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            Text("Training 600 episodes locally on this device…")
        }

        result?.let { trained ->
            val latest = trained.episodes.last()
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Metric("Episodes", trained.episodes.size.toString())
                Metric("Success", "${(trained.successRate * 100).toInt()}%")
                Metric("Epsilon", "%.3f".format(trained.finalEpsilon))
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Metric("Last reward", "%.1f".format(latest.reward))
                Metric("Last steps", latest.steps.toString())
                Metric("Best path", trained.bestPath.size.toString())
            }
            RewardChart(
                episodes = trained.episodes,
                modifier = Modifier.fillMaxWidth().aspectRatio(2.4f),
            )
            Text(
                "The chart shows episode reward over time. Q-Learning is stochastic training, unlike A* and Dijkstra which will deterministically search the maze.",
                style = MaterialTheme.typography.bodySmall,
            )
        }

        Button(
            enabled = !isTraining,
            onClick = {
                isTraining = true
                result = null
                scope.launch {
                    result = withContext(Dispatchers.Default) {
                        QLearningTrainer.train(
                            maze = maze,
                            config = QLearningConfig(
                                episodes = 600,
                                maxStepsPerEpisode = 400,
                            ),
                            seed = 42L,
                        )
                    }
                    isTraining = false
                }
            },
        ) {
            Text(if (result == null) "Train Q-Learning agent" else "Train again")
        }
    }
}

@Composable
private fun Metric(label: String, value: String) {
    Column {
        Text(value, style = MaterialTheme.typography.titleMedium)
        Text(label, style = MaterialTheme.typography.labelSmall)
    }
}

@Composable
private fun RewardChart(
    episodes: List<EpisodeMetric>,
    modifier: Modifier = Modifier,
) {
    val lineColor = MaterialTheme.colorScheme.primary
    val axisColor = MaterialTheme.colorScheme.outline
    Canvas(modifier = modifier) {
        if (episodes.size < 2) return@Canvas
        val rewards = episodes.map { it.reward }
        val minReward = rewards.minOrNull() ?: 0.0
        val maxReward = rewards.maxOrNull() ?: 1.0
        val range = (maxReward - minReward).takeIf { it > 0.0 } ?: 1.0
        drawLine(axisColor, Offset(0f, size.height), Offset(size.width, size.height), 2f)
        for (index in 0 until episodes.lastIndex) {
            fun point(i: Int): Offset {
                val x = i.toFloat() / episodes.lastIndex * size.width
                val normalized = ((episodes[i].reward - minReward) / range).toFloat()
                val y = size.height - normalized * size.height
                return Offset(x, y)
            }
            drawLine(lineColor, point(index), point(index + 1), 2.5f)
        }
    }
}
