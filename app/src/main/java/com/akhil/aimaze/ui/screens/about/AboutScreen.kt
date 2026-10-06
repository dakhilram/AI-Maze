package com.akhil.aimaze.ui.screens.about

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.akhil.aimaze.R
import com.akhil.aimaze.ui.screens.common.PlaceholderScreen

@Composable
fun AboutScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    PlaceholderScreen(
        titleRes = R.string.about_title,
        descriptionRes = R.string.about_description,
        onBack = onBack,
        modifier = modifier,
    )
}
