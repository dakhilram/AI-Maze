package com.akhil.aimaze.ui.screens.history

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.akhil.aimaze.data.history.AiMazeDatabase
import com.akhil.aimaze.data.history.BenchmarkHistoryEntity
import com.akhil.aimaze.data.history.BenchmarkHistoryRepository
import java.text.DateFormat
import java.util.Date
import kotlinx.coroutines.launch

@Composable
fun HistoryScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val repository = remember(context) {
        BenchmarkHistoryRepository(AiMazeDatabase.get(context).benchmarkHistoryDao())
    }
    val history by repository.observeAll().collectAsState(initial = emptyList())
    val scope = rememberCoroutineScope()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            OutlinedButton(onClick = onBack) { Text("Back") }
            if (history.isNotEmpty()) {
                OutlinedButton(onClick = { scope.launch { repository.clearAll() } }) {
                    Text("Clear history")
                }
            }
        }
        Text("Learning / Benchmark History", style = MaterialTheme.typography.headlineMedium)
        Text(
            "Benchmark snapshots are stored only on this device. Nothing is uploaded.",
            style = MaterialTheme.typography.bodyLarge,
        )

        if (history.isEmpty()) {
            Card(modifier = Modifier.fillMaxWidth()) {
                Text(
                    "No benchmark runs yet. Run AI vs Algorithms to create your first local record.",
                    modifier = Modifier.padding(16.dp),
                )
            }
        } else {
            history.forEach { item -> HistoryCard(item) }
        }
    }
}

@Composable
private fun HistoryCard(item: BenchmarkHistoryEntity) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Text(
                "${item.rows}×${item.columns} • seed ${item.mazeSeed}",
                style = MaterialTheme.typography.titleMedium,
            )
            Text(
                DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT).format(Date(item.createdAt)),
                style = MaterialTheme.typography.labelMedium,
            )
            Text("A*: path ${item.aStarPathLength}, explored ${item.aStarNodesExplored}")
            Text("Dijkstra: path ${item.dijkstraPathLength}, explored ${item.dijkstraNodesExplored}")
            Text(
                "Q-Learning: ${item.qLearningEpisodes} episodes, ${(item.qLearningSuccessRate * 100).toInt()}% success, learned path ${item.qLearningPathLength ?: "n/a"}",
            )
            Text(
                "Random: ${item.randomTrials} trials, ${(item.randomSuccessRate * 100).toInt()}% success, best path ${item.randomBestPathLength ?: "n/a"}",
            )
        }
    }
}
