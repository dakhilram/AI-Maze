package com.akhil.aimaze.ui.screens.training

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.akhil.aimaze.R
import com.akhil.aimaze.ui.screens.common.PlaceholderScreen

@Composable
fun TrainingScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    PlaceholderScreen(
        titleRes = R.string.training_title,
        descriptionRes = R.string.training_description,
        onBack = onBack,
        modifier = modifier,
    )
}
