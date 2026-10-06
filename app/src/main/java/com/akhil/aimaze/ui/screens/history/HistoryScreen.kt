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
import com.akhil.aimaze.data.history.RaceRecordEntity
import com.akhil.aimaze.data.history.RaceRecordRepository
import com.akhil.aimaze.data.history.SoloRunRecordEntity
import com.akhil.aimaze.data.history.SoloRunRecordRepository
import java.text.DateFormat
import java.util.Date
import kotlinx.coroutines.launch

@Composable
fun HistoryScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val database = remember(context) { AiMazeDatabase.get(context) }
    val raceRepository = remember(database) { RaceRecordRepository(database.raceRecordDao()) }
    val soloRepository = remember(database) { SoloRunRecordRepository(database.soloRunRecordDao()) }

    val races by raceRepository.observeAll().collectAsState(initial = emptyList())
    val soloRuns by soloRepository.observeAll().collectAsState(initial = emptyList())
    val scope = rememberCoroutineScope()
    val isEmpty = races.isEmpty() && soloRuns.isEmpty()

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
                if (!isEmpty) {
                    OutlinedButton(
                        onClick = {
                            scope.launch {
                                raceRepository.clearAll()
                                soloRepository.clearAll()
                            }
                        },
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
                "Your runs are stored only on this device.",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            if (isEmpty) {
                EmptyRecords()
            } else {
                val wins = races.count { it.winner == "PLAYER" }
                val stars = soloRuns.sumOf { it.stars }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    SummaryCard("CLEARS", soloRuns.size.toString(), Modifier.weight(1f))
                    SummaryCard("RACE WINS", wins.toString(), Modifier.weight(1f))
                    SummaryCard("STARS", stars.toString(), Modifier.weight(1f))
                }

                if (soloRuns.isNotEmpty()) {
                    SectionTitle("SOLO RUNS")
                    soloRuns.take(12).forEach { SoloRecordCard(it) }
                }

                if (races.isNotEmpty()) {
                    SectionTitle("BEAT THE BOT")
                    races.take(12).forEach { RaceRecordCard(it) }
                }
            }
        }
    }
}

@Composable
private fun EmptyRecords() {
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
                "Clear a Maze Run, finish a Time Attack, or race the bot to create your first record.",
                style = MaterialTheme.typography.bodyLarge,
            )
        }
    }
}

@Composable
private fun SectionTitle(text: String) {
    Text(
        text,
        style = MaterialTheme.typography.labelLarge,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        fontWeight = FontWeight.Black,
    )
}

@Composable
private fun SummaryCard(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
) {
    Card(modifier = modifier, shape = RoundedCornerShape(16.dp)) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(value, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Black)
            Text(
                label,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
            )
        }
    }
}

@Composable
private fun SoloRecordCard(item: SoloRunRecordEntity) {
    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(22.dp)) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(7.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    if (item.mode == "TIME_ATTACK") "TIME ATTACK" else "MAZE RUN",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Black,
                )
                Text(
                    "★".repeat(item.stars) + "☆".repeat(3 - item.stars),
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Black,
                )
            }
            Text(
                "${item.rows}×${item.columns} • Level #${item.mazeSeed}",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text("${item.moves} moves • ${formatDuration(item.elapsedMs)}")
            RecordDate(item.createdAt)
        }
    }
}

@Composable
private fun RaceRecordCard(item: RaceRecordEntity) {
    val playerWon = item.winner == "PLAYER"
    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(22.dp)) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(7.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    "${item.rows}×${item.columns} • Level #${item.mazeSeed}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Black,
                )
                Text(
                    if (playerWon) "WIN" else "LOSS",
                    color = if (playerWon) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Black,
                )
            }
            Text("You: ${item.playerMoves} moves • Bot: ${item.botSteps} steps")
            RecordDate(item.createdAt)
        }
    }
}

@Composable
private fun RecordDate(timestamp: Long) {
    Text(
        DateFormat.getDateTimeInstance(
            DateFormat.MEDIUM,
            DateFormat.SHORT,
        ).format(Date(timestamp)),
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        style = MaterialTheme.typography.bodySmall,
    )
}

private fun formatDuration(ms: Long): String {
    val totalSeconds = ms / 1_000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return "%d:%02d".format(minutes, seconds)
}
