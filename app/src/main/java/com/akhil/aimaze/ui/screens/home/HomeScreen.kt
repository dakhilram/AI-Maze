package com.akhil.aimaze.ui.screens.home

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.akhil.aimaze.ui.game.GameBackdrop
import com.akhil.aimaze.ui.game.GameBackdropStyle
import com.akhil.aimaze.ui.game.GamePreferences
import com.akhil.aimaze.ui.game.rememberGameFeedback
import com.akhil.aimaze.ui.navigation.AppDestination
import kotlinx.coroutines.delay

@Composable
fun HomeScreen(
    destinations: List<AppDestination>,
    onDestinationSelected: (AppDestination) -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val feedback = rememberGameFeedback()
    val campaignLevel = GamePreferences(context).campaignLevel
    val play = destinations.firstOrNull { it == AppDestination.Play }
    val daily = destinations.firstOrNull { it == AppDestination.Daily }
    val timeAttack = destinations.firstOrNull { it == AppDestination.TimeAttack }
    val beatBot = destinations.firstOrNull { it == AppDestination.Comparison }
    val records = destinations.firstOrNull { it == AppDestination.History }
    val settings = destinations.firstOrNull { it == AppDestination.Settings }
    val howToPlay = destinations.firstOrNull { it == AppDestination.About }

    var revealed by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        delay(80)
        revealed = true
    }
    val contentAlpha by animateFloatAsState(
        targetValue = if (revealed) 1f else 0f,
        animationSpec = tween(420, easing = FastOutSlowInEasing),
        label = "homeAlpha",
    )
    val contentOffset by animateFloatAsState(
        targetValue = if (revealed) 0f else 34f,
        animationSpec = tween(420, easing = FastOutSlowInEasing),
        label = "homeOffset",
    )

    GameBackdrop(
        style = GameBackdropStyle.Home,
        modifier = modifier,
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    alpha = contentAlpha
                    translationY = contentOffset
                },
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
                    text = "500 handcrafted-feeling procedural levels, daily challenges, timed runs, and bot races.",
                    modifier = Modifier.padding(top = 8.dp),
                    color = Color.White.copy(alpha = 0.72f),
                    style = MaterialTheme.typography.bodyLarge,
                )
            }

            play?.let { destination ->
                item {
                    HeroModeCard(
                        title = "CONTINUE • LEVEL $campaignLevel",
                        subtitle = "${GamePreferences.CAMPAIGN_LEVELS} levels to beat • more coming soon",
                        cta = "PLAY",
                        progress = campaignLevel.toFloat() / GamePreferences.CAMPAIGN_LEVELS,
                        gradient = Brush.linearGradient(
                            listOf(
                                Color(0xFFFF8A1F),
                                Color(0xFFC44A00),
                            ),
                        ),
                        onClick = {
                            feedback.button()
                            onDestinationSelected(destination)
                        },
                    )
                }
            }

            daily?.let { destination ->
                item {
                    UtilityCard(
                        modifier = Modifier.fillMaxWidth(),
                        title = "DAILY CHALLENGE",
                        subtitle = "One maze for today • score moves + speed",
                        onClick = {
                            feedback.button()
                            onDestinationSelected(destination)
                        },
                    )
                }
            }

            item {
                Text(
                    text = "CHALLENGE MODES",
                    color = Color.White.copy(alpha = 0.62f),
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
                            tint = Color(0xFF315FD7),
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
                            tint = Color(0xFF7047C7),
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
                        subtitle = "Campaign clears • stars • times • race wins",
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
    progress: Float,
    gradient: Brush,
    onClick: () -> Unit,
) {
    val shape = RoundedCornerShape(30.dp)
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(206.dp)
            .clip(shape)
            .background(gradient)
            .border(1.dp, Color.White.copy(alpha = 0.16f), shape)
            .clickable(onClick = onClick)
            .padding(22.dp),
    ) {
        Column(
            modifier = Modifier.align(Alignment.BottomStart),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                text = title,
                color = Color.White,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Black,
            )
            Text(
                text = subtitle,
                color = Color.White.copy(alpha = 0.82f),
                style = MaterialTheme.typography.bodyMedium,
            )
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier.fillMaxWidth(0.70f),
                color = Color.White,
                trackColor = Color.Black.copy(alpha = 0.18f),
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
                        tint,
                        Color(0xE810141C),
                    ),
                ),
            )
            .border(1.dp, Color.White.copy(alpha = 0.10f), shape)
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
                color = Color.White.copy(alpha = 0.68f),
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
            .background(Color(0xD9181F2A))
            .border(1.dp, Color.White.copy(alpha = 0.10f), shape)
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
            color = Color.White.copy(alpha = 0.62f),
            style = MaterialTheme.typography.bodySmall,
        )
    }
}
