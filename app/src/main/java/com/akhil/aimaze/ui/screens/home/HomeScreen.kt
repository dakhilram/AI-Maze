package com.akhil.aimaze.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.weight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.akhil.aimaze.R
import com.akhil.aimaze.ui.game.GameBackdrop
import com.akhil.aimaze.ui.game.GameBackdropStyle
import com.akhil.aimaze.ui.game.rememberGameFeedback
import com.akhil.aimaze.ui.navigation.AppDestination

@Composable
fun HomeScreen(
    destinations: List<AppDestination>,
    onDestinationSelected: (AppDestination) -> Unit,
    modifier: Modifier = Modifier,
) {
    val feedback = rememberGameFeedback()
    val play = destinations.firstOrNull { it == AppDestination.Play }
    val timeAttack = destinations.firstOrNull { it == AppDestination.TimeAttack }
    val beatBot = destinations.firstOrNull { it == AppDestination.Comparison }
    val records = destinations.firstOrNull { it == AppDestination.History }
    val settings = destinations.firstOrNull { it == AppDestination.Settings }
    val howToPlay = destinations.firstOrNull { it == AppDestination.About }

    GameBackdrop(
        style = GameBackdropStyle.Home,
        modifier = modifier,
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 26.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            item {
                Text(
                    text = "MAZE RUSH",
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Black,
                )
                Text(
                    text = "Escape faster.",
                    modifier = Modifier.padding(top = 4.dp),
                    color = Color.White,
                    style = MaterialTheme.typography.displaySmall,
                    fontWeight = FontWeight.Black,
                )
                Text(
                    text = "Swipe through shifting mazes, beat the clock, and outrun the bot.",
                    modifier = Modifier.padding(top = 8.dp),
                    color = Color.White.copy(alpha = 0.72f),
                    style = MaterialTheme.typography.bodyLarge,
                )
            }

            play?.let { destination ->
                item {
                    HeroModeCard(
                        title = "MAZE RUN",
                        subtitle = "Classic run • stars • par score",
                        cta = "PLAY",
                        gradient = Brush.linearGradient(
                            listOf(
                                Color(0xFFFF8A1F),
                                Color(0xFFB84E00),
                            ),
                        ),
                        onClick = {
                            feedback.button()
                            onDestinationSelected(destination)
                        },
                    )
                }
            }

            item {
                Text(
                    text = "CHALLENGES",
                    color = Color.White.copy(alpha = 0.6f),
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Black,
                )
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    timeAttack?.let { destination ->
                        ChallengeCard(
                            modifier = Modifier.weight(1f),
                            badge = "⏱",
                            title = "TIME ATTACK",
                            subtitle = "Race the clock",
                            tint = Color(0xFF4F7CFF),
                            onClick = {
                                feedback.button()
                                onDestinationSelected(destination)
                            },
                        )
                    }
                    beatBot?.let { destination ->
                        ChallengeCard(
                            modifier = Modifier.weight(1f),
                            badge = "⚡",
                            title = "BEAT THE BOT",
                            subtitle = "Live maze race",
                            tint = Color(0xFF8B5CF6),
                            onClick = {
                                feedback.button()
                                onDestinationSelected(destination)
                            },
                        )
                    }
                }
            }

            item {
                records?.let { destination ->
                    UtilityCard(
                        modifier = Modifier.fillMaxWidth(),
                        title = "RECORDS",
                        subtitle = "Wins • stars • times",
                        onClick = {
                            feedback.button()
                            onDestinationSelected(destination)
                        },
                    )
                }
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    settings?.let { destination ->
                        UtilityCard(
                            modifier = Modifier.weight(1f),
                            title = "SETTINGS",
                            subtitle = "Sound • haptics",
                            onClick = {
                                feedback.button()
                                onDestinationSelected(destination)
                            },
                        )
                    }
                    howToPlay?.let { destination ->
                        UtilityCard(
                            modifier = Modifier.weight(1f),
                            title = "HOW TO PLAY",
                            subtitle = "Modes • scoring",
                            onClick = {
                                feedback.button()
                                onDestinationSelected(destination)
                            },
                        )
                    }
                }
            }

            item {
                Spacer(Modifier.height(6.dp))
                Text(
                    text = "OFFLINE • NO ADS • NO ACCOUNT",
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White.copy(alpha = 0.45f),
                )
            }
        }
    }
}

@Composable
private fun HeroModeCard(
    title: String,
    subtitle: String,
    cta: String,
    gradient: Brush,
    onClick: () -> Unit,
) {
    val shape = RoundedCornerShape(30.dp)
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(190.dp)
            .clip(shape)
            .background(gradient)
            .clickable(onClick = onClick)
            .padding(22.dp),
    ) {
        Column(
            modifier = Modifier.align(Alignment.BottomStart),
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Text(
                text = title,
                color = Color.White,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Black,
            )
            Text(
                text = subtitle,
                color = Color.White.copy(alpha = 0.78f),
                style = MaterialTheme.typography.bodyMedium,
            )
        }

        Surface(
            color = Color.Black.copy(alpha = 0.26f),
            shape = RoundedCornerShape(999.dp),
            modifier = Modifier.align(Alignment.TopEnd),
        ) {
            Text(
                text = cta,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 9.dp),
                color = Color.White,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Black,
            )
        }
    }
}

@Composable
private fun ChallengeCard(
    modifier: Modifier = Modifier,
    badge: String,
    title: String,
    subtitle: String,
    tint: Color,
    onClick: () -> Unit,
) {
    val shape = RoundedCornerShape(24.dp)
    Column(
        modifier = modifier
            .height(176.dp)
            .clip(shape)
            .background(
                Brush.verticalGradient(
                    listOf(
                        tint.copy(alpha = 0.88f),
                        Color(0xFF111722),
                    ),
                ),
            )
            .border(1.dp, Color.White.copy(alpha = 0.08f), shape)
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = badge,
            style = MaterialTheme.typography.headlineMedium,
        )
        Column {
            Text(
                text = title,
                color = Color.White,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Black,
            )
            Text(
                text = subtitle,
                color = Color.White.copy(alpha = 0.62f),
                style = MaterialTheme.typography.bodySmall,
            )
        }
    }
}

@Composable
private fun UtilityCard(
    modifier: Modifier = Modifier,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
) {
    val shape = RoundedCornerShape(20.dp)
    Column(
        modifier = modifier
            .clip(shape)
            .background(Color(0xB31A2230))
            .border(1.dp, Color.White.copy(alpha = 0.08f), shape)
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(5.dp),
    ) {
        Text(
            text = title,
            color = Color.White,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Black,
        )
        Text(
            text = subtitle,
            color = Color.White.copy(alpha = 0.58f),
            style = MaterialTheme.typography.bodySmall,
        )
    }
}
