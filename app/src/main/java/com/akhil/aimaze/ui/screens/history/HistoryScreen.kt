package com.akhil.aimaze.ui.screens.history

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
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
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                OutlinedButton(onClick = onBack, shape = RoundedCornerShape(16.dp)) {
                    Text("‹")
                }
                if (history.isNotEmpty()) {
                    OutlinedButton(
                        onClick = { scope.launch { repository.clearAll() } },
                        shape = RoundedCornerShape(16.dp),
                    ) {
                        Text("CLEAR")
                    }
                }
            }

            Text(
                "Records",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Black,
            )
            Text(
                "Your saved race results live only on this device.",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            if (history.isEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    ),
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp),
                    ) {
                        Text(
                            "NO RECORDS YET",
                            color = MaterialTheme.colorScheme.primary,
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Black,
                        )
                        Text(
                            "Run Race Mode to create your first scoreboard entry.",
                            style = MaterialTheme.typography.bodyLarge,
                        )
                    }
                }
            } else {
                history.forEachIndexed { index, item ->
                    RecordCard(
                        rank = index + 1,
                        item = item,
                    )
                }
            }
        }
    }
}

@Composable
private fun RecordCard(
    rank: Int,
    item: BenchmarkHistoryEntity,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
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
                    "#$rank  •  ${item.rows}×${item.columns}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Black,
                )
                Text(
                    "LEVEL ${item.mazeSeed}",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold,
                )
            }
            Text(
                DateFormat.getDateTimeInstance(
                    DateFormat.MEDIUM,
                    DateFormat.SHORT,
                ).format(Date(item.createdAt)),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text("A*  •  path ${item.aStarPathLength}  •  explored ${item.aStarNodesExplored}")
            Text("Dijkstra  •  path ${item.dijkstraPathLength}  •  explored ${item.dijkstraNodesExplored}")
            Text(
                "Q-Learning  •  ${(item.qLearningSuccessRate * 100).toInt()}% success  •  path ${item.qLearningPathLength ?: "—"}",
            )
            Text(
                "Random  •  ${(item.randomSuccessRate * 100).toInt()}% success  •  best ${item.randomBestPathLength ?: "—"}",
            )
        }
    }
}
