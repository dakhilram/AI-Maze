package com.akhil.aimaze.ui.screens.comparison

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.akhil.aimaze.R
import com.akhil.aimaze.ui.screens.common.PlaceholderScreen

@Composable
fun ComparisonScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    PlaceholderScreen(
        titleRes = R.string.comparison_title,
        descriptionRes = R.string.comparison_description,
        onBack = onBack,
        modifier = modifier,
    )
}
