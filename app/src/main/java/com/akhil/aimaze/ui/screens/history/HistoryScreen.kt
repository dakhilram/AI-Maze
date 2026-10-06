package com.akhil.aimaze.ui.screens.history

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.akhil.aimaze.R
import com.akhil.aimaze.ui.screens.common.PlaceholderScreen

@Composable
fun HistoryScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    PlaceholderScreen(
        titleRes = R.string.history_title,
        descriptionRes = R.string.history_description,
        onBack = onBack,
        modifier = modifier,
    )
}
