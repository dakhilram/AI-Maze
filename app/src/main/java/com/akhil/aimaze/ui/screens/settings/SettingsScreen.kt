package com.akhil.aimaze.ui.screens.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.akhil.aimaze.ui.game.GameBackdrop
import com.akhil.aimaze.ui.game.GameBackdropStyle
import com.akhil.aimaze.ui.game.GameBackButton
import com.akhil.aimaze.ui.game.GamePreferences
import com.akhil.aimaze.ui.game.rememberGameFeedback

@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val preferences = remember(context) { GamePreferences(context) }
    val feedback = rememberGameFeedback()
    var soundEnabled by remember { mutableStateOf(preferences.soundEnabled) }
    var hapticsEnabled by remember { mutableStateOf(preferences.hapticsEnabled) }

    GameBackdrop(
        style = GameBackdropStyle.Gameplay,
        modifier = modifier,
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            GameBackButton(
                onClick = {
                    feedback.button()
                    onBack()
                },
            )

            Text(
                "Settings",
                color = Color.White,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Black,
            )
            Text(
                "Tune the game feedback to your device.",
                color = Color.White.copy(alpha = 0.62f),
            )

            SettingCard(
                title = "Sound",
                subtitle = "Movement, countdown, win, loss, and menu sounds",
                checked = soundEnabled,
                onCheckedChange = { checked ->
                    soundEnabled = checked
                    preferences.soundEnabled = checked
                    if (checked) feedback.button()
                },
            )

            SettingCard(
                title = "Haptics",
                subtitle = "Vibration feedback for movement and collisions",
                checked = hapticsEnabled,
                onCheckedChange = { checked ->
                    hapticsEnabled = checked
                    preferences.hapticsEnabled = checked
                },
            )
        }
    }
}

@Composable
private fun SettingCard(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Black,
                )
                Text(
                    subtitle,
                    modifier = Modifier.padding(top = 4.dp),
                    color = Color.White.copy(alpha = 0.62f),
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange,
            )
        }
    }
}
