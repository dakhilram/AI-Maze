package com.akhil.aimaze.ui.screens.play

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.akhil.aimaze.R
import com.akhil.aimaze.ui.screens.common.PlaceholderScreen

@Composable
fun PlayMazeScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    PlaceholderScreen(
        titleRes = R.string.play_title,
        descriptionRes = R.string.play_description,
        onBack = onBack,
        modifier = modifier,
    )
}
