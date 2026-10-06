package com.akhil.aimaze.ui.screens.home

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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.akhil.aimaze.R
import com.akhil.aimaze.ui.navigation.AppDestination
import com.akhil.aimaze.ui.theme.AIMazeTheme

@Composable
fun HomeScreen(
    destinations: List<AppDestination>,
    onDestinationSelected: (AppDestination) -> Unit,
    modifier: Modifier = Modifier,
) {
    val play = destinations.firstOrNull { it == AppDestination.Play }
    val secondaryModes = destinations.filterNot { it == AppDestination.Play }

    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background,
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.TopCenter,
        ) {
            LazyColumn(
                modifier = Modifier
                    .widthIn(max = 720.dp)
                    .fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 28.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                item {
                    GameHeader()
                }

                play?.let { destination ->
                    item {
                        PrimaryPlayCard(
                            destination = destination,
                            onClick = { onDestinationSelected(destination) },
                        )
                    }
                }

                item {
                    Text(
                        text = "GAME MODES",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 8.dp),
                    )
                }

                items(
                    items = secondaryModes,
                    key = { it.route },
                ) { destination ->
                    GameModeCard(
                        destination = destination,
                        onClick = { onDestinationSelected(destination) },
                    )
                }

                item {
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = "Offline • No account • No ads",
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }
    }
}

@Composable
private fun GameHeader() {
    Column {
        Text(
            text = stringResource(R.string.home_eyebrow),
            color = MaterialTheme.colorScheme.primary,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Black,
        )
        Text(
            text = stringResource(R.string.home_title),
            modifier = Modifier.padding(top = 4.dp),
            style = MaterialTheme.typography.displaySmall,
            fontWeight = FontWeight.Black,
        )
        Text(
            text = stringResource(R.string.home_description),
            modifier = Modifier.padding(top = 10.dp),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.bodyLarge,
        )
    }
}

@Composable
private fun PrimaryPlayCard(
    destination: AppDestination,
    onClick: () -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
        ),
    ) {
        Column(
            modifier = Modifier.padding(22.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Surface(
                shape = RoundedCornerShape(999.dp),
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.16f),
            ) {
                Text(
                    text = "QUICK PLAY",
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                )
            }

            Text(
                text = stringResource(destination.titleRes),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Black,
            )
            Text(
                text = stringResource(destination.descriptionRes),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodyLarge,
            )

            Button(
                onClick = onClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .semantics { role = Role.Button },
                shape = RoundedCornerShape(18.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                ),
            ) {
                Text(
                    text = "PLAY NOW",
                    fontWeight = FontWeight.Black,
                )
            }
        }
    }
}

@Composable
private fun GameModeCard(
    destination: AppDestination,
    onClick: () -> Unit,
) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .semantics { role = Role.Button },
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Row(
            modifier = Modifier.padding(18.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Surface(
                modifier = Modifier
                    .size(44.dp)
                    .clearAndSetSemantics { },
                shape = RoundedCornerShape(14.dp),
                color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.16f),
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = modeGlyph(destination),
                        color = MaterialTheme.colorScheme.secondary,
                        fontWeight = FontWeight.Black,
                        style = MaterialTheme.typography.titleMedium,
                    )
                }
            }

            Spacer(Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = stringResource(destination.titleRes),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = stringResource(destination.descriptionRes),
                    modifier = Modifier.padding(top = 3.dp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodyMedium,
                )
            }

            Spacer(Modifier.width(10.dp))
            Text(
                text = "›",
                modifier = Modifier.clearAndSetSemantics { },
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.primary,
            )
        }
    }
}

private fun modeGlyph(destination: AppDestination): String = when (destination) {
    AppDestination.TimeAttack -> "⏱"
    AppDestination.Comparison -> "⚡"
    AppDestination.History -> "★"
    AppDestination.About -> "?"
    else -> "•"
}

@Preview(showBackground = true)
@Composable
private fun HomeScreenPreview() {
    AIMazeTheme(darkTheme = true) {
        HomeScreen(
            destinations = AppDestination.homeSections,
            onDestinationSelected = {},
        )
    }
}
