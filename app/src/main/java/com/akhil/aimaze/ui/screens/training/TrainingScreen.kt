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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.text.font.FontWeight
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
                        "Training Lab",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Black,
                    )
                    Text(
                        "Teach the solver to escape this maze.",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
            }

            Card(
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                ),
            ) {
                MazeBoard(
                    maze = maze,
                    path = result?.bestPath.orEmpty().take(visiblePathSteps),
                    player = result?.bestPath?.getOrNull((visiblePathSteps - 1).coerceAtLeast(0)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp)
                        .aspectRatio(1f),
                )
            }

            if (isTraining) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Text(
                            "TRAINING IN PROGRESS",
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Black,
                        )
                        LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                        Text(
                            "Running 600 learning rounds on this device.",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            }

            result?.let { trained ->
                val latest = trained.episodes.last()
                Text(
                    "RUN RESULTS",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Black,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    MetricCard("SUCCESS", "${(trained.successRate * 100).toInt()}%", Modifier.weight(1f))
                    MetricCard("BEST PATH", trained.bestPath.size.toString(), Modifier.weight(1f))
                    MetricCard("EPSILON", "%.3f".format(trained.finalEpsilon), Modifier.weight(1f))
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    MetricCard("REWARD", "%.1f".format(latest.reward), Modifier.weight(1f))
                    MetricCard("STEPS", latest.steps.toString(), Modifier.weight(1f))
                    MetricCard("ROUNDS", trained.episodes.size.toString(), Modifier.weight(1f))
                }
                RewardChart(
                    episodes = trained.episodes,
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(2.4f),
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
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
            ) {
                Text(
                    if (result == null) "START TRAINING" else "TRAIN AGAIN",
                    fontWeight = FontWeight.Black,
                )
            }
        }
    }
}

@Composable
private fun MetricCard(
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
                value,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Black,
            )
            Text(
                label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.Bold,
            )
        }
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
