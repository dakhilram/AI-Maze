package com.akhil.aimaze.ui.screens.about

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun AboutScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
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
            OutlinedButton(onClick = onBack, shape = RoundedCornerShape(16.dp)) {
                Text("‹")
            }

            Text(
                "Game Guide",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Black,
            )
            Text(
                "Everything runs offline. No account, no servers, no ads.",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            GuideCard(
                title = "MAZE RUN",
                body = "Swipe directly on the maze. Each swipe attempts one move. Walls block movement. Reach the goal in as few moves as you can.",
            )
            GuideCard(
                title = "TRAINING LAB",
                body = "The learning solver improves through repeated attempts. It gets rewarded for reaching the exit and penalized for wasted or invalid moves.",
            )
            GuideCard(
                title = "RACE MODE",
                body = "A*, Dijkstra, the trained solver, and a Random baseline all face the same maze. Their score types stay separate so the comparison remains fair.",
            )
            GuideCard(
                title = "MAZE GENERATION",
                body = "Each level is generated with randomized depth-first search. The level number is a reproducible seed, so the same level always recreates the same maze.",
            )
            GuideCard(
                title = "A*",
                body = "A* uses the distance already travelled plus a goal-directed estimate. On this grid it still returns an optimal route.",
            )
            GuideCard(
                title = "DIJKSTRA",
                body = "Dijkstra searches outward by shortest known distance. It also finds the optimal route, usually after exploring more cells.",
            )
            GuideCard(
                title = "Q-LEARNING",
                body = "Each cell is a state and each direction is an action. The solver updates Q-values after every move, gradually learning which decisions lead to the exit.",
            )
            GuideCard(
                title = "RANDOM",
                body = "The Random runner picks among valid neighboring cells. It is a deliberately weak baseline used for context, not a serious shortest-path solver.",
            )
        }
    }
}

@Composable
private fun GuideCard(
    title: String,
    body: String,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                title,
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Black,
            )
            Text(
                body,
                style = MaterialTheme.typography.bodyMedium,
            )
        }
    }
}
