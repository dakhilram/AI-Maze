package com.akhil.aimaze.ui.screens.about

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun AboutScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        OutlinedButton(onClick = onBack) { Text("Back") }
        Text("How It Works", style = MaterialTheme.typography.headlineMedium)
        Text(
            "AI Maze is an offline educational app. Every maze, training episode, benchmark, and saved result is computed locally on the Android device.",
            style = MaterialTheme.typography.bodyLarge,
        )

        Explanation(
            "Maze generation",
            "Randomized depth-first search (recursive backtracker) starts with every wall closed, walks to unvisited neighbors, carves reciprocal passages, and backtracks when stuck. The result is a perfect maze: connected and acyclic, with exactly one route between any two cells. A seed makes generation reproducible.",
        )
        Explanation(
            "Q-Learning",
            "The reinforcement-learning agent treats each cell as a state and the four cardinal directions as actions. Q-values are updated from rewards using a learning rate and discount factor. Reaching the goal earns a large positive reward; steps and invalid moves carry penalties.",
        )
        Explanation(
            "Exploration vs exploitation",
            "An epsilon-greedy policy sometimes explores a random action and otherwise chooses the best-known action. Epsilon decays during training so early episodes explore broadly while later episodes increasingly exploit learned values.",
        )
        Explanation(
            "A*",
            "A* is deterministic graph search. It combines the path cost already travelled with a Manhattan-distance heuristic toward the goal. On this unit-cost grid, the heuristic is admissible and A* returns an optimal path.",
        )
        Explanation(
            "Dijkstra",
            "Dijkstra also finds an optimal route but does not use a goal-directed heuristic. It expands positions in increasing path-cost order, so it may explore more of the maze than A*.",
        )
        Explanation(
            "Random baseline",
            "The Random agent simply selects among currently valid neighboring cells. It is intentionally weak and stochastic. Its success rate and step counts provide context, not a claim that it is equivalent to a shortest-path solver.",
        )
        Explanation(
            "Fair benchmarking",
            "A* and Dijkstra are compared using path length, nodes explored, and one-shot search time. Q-Learning is reported with training episodes, training success rate, exploration rate, and learned path quality. Random is reported across repeated trials. Training time is not presented as the same metric as deterministic search time.",
        )
    }
}

@Composable
private fun Explanation(title: String, body: String) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(title, style = MaterialTheme.typography.titleMedium)
            Text(body, style = MaterialTheme.typography.bodyMedium)
        }
    }
}
