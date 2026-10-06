package com.akhil.aimaze.ui.navigation

import androidx.annotation.StringRes
import com.akhil.aimaze.R

enum class AppDestination(
    val route: String,
    @field:StringRes val titleRes: Int,
    @field:StringRes val descriptionRes: Int,
) {
    Home(
        route = "home",
        titleRes = R.string.app_name,
        descriptionRes = R.string.home_description,
    ),
    Play(
        route = "play",
        titleRes = R.string.play_title,
        descriptionRes = R.string.play_description,
    ),
    Training(
        route = "training",
        titleRes = R.string.training_title,
        descriptionRes = R.string.training_description,
    ),
    Comparison(
        route = "comparison",
        titleRes = R.string.comparison_title,
        descriptionRes = R.string.comparison_description,
    ),
    History(
        route = "history",
        titleRes = R.string.history_title,
        descriptionRes = R.string.history_description,
    ),
    About(
        route = "about",
        titleRes = R.string.about_title,
        descriptionRes = R.string.about_description,
    ),
    ;

    companion object {
        val homeSections: List<AppDestination> = listOf(Play, Comparison, Training, History, About)
    }
}
