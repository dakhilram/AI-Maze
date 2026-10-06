package com.akhil.aimaze.ui.screens.about

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.akhil.aimaze.ui.game.GameBackdrop
import com.akhil.aimaze.ui.game.GameBackdropStyle
import com.akhil.aimaze.ui.game.GameBackButton

@Composable
fun AboutScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    GameBackdrop(
        style = GameBackdropStyle.Gameplay,
        modifier = modifier,
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            GameBackButton(onClick = onBack)

            Text(
                "How to Play",
                color = Color.White,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Black,
            )
            Text(
                "Swipe directly on the maze. Reach the glowing exit and keep your route clean.",
                style = MaterialTheme.typography.bodyLarge,
                color = Color.White.copy(alpha = 0.62f),
            )

            GuideCard(
                title = "MAZE RUN",
                body = "A 500-level campaign. Your timer starts on the first successful move. Match the TARGET move count for 3 stars and unlock the next level.",
            )
            GuideCard(
                title = "DAILY CHALLENGE",
                body = "Every day gets one fixed 12×12 maze generated from the date. Score it with both route efficiency and completion speed.",
            )
            GuideCard(
                title = "TIME ATTACK",
                body = "You get a fixed clock based on difficulty. A 3-2-1 countdown starts the round. Escape before time expires.",
            )
            GuideCard(
                title = "BEAT THE BOT",
                body = "You and the blue bot race the exact same maze. Swipe fast, avoid wasted moves, and reach the exit first.",
            )
            GuideCard(
                title = "DIFFICULTY",
                body = "Campaign difficulty rises automatically: levels 1–125 use 8×8 mazes, 126–250 use 12×12, 251–375 use 16×16, and 376–500 use 20×20. Challenge modes let you pick difficulty directly.",
            )
            GuideCard(
                title = "SCORING",
                body = "TARGET is the shortest valid route for that campaign maze. Match it for 3 stars, stay close for 2, and every clear earns at least 1 star. Time and race modes reward speed.",
            )
            GuideCard(
                title = "FEEDBACK",
                body = "Successful moves animate smoothly. Hitting a wall gives a shake, sound, and haptic response. Wins and losses have their own feedback.",
            )
            GuideCard(
                title = "OFFLINE",
                body = "Maze Rush runs fully on-device. No account, no ads, no internet connection, and no online leaderboard are required.",
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
            Text(body, style = MaterialTheme.typography.bodyMedium)
        }
    }
}
