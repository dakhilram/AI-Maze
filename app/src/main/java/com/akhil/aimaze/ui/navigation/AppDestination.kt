package com.akhil.aimaze.ui.navigation

import androidx.annotation.StringRes
import com.akhil.aimaze.R

enum class AppDestination(
    val route: String,
    @field:StringRes val titleRes: Int,
    @field:StringRes val descriptionRes: Int,
) {
    Home("home", R.string.app_name, R.string.home_description),
    Play("play", R.string.play_title, R.string.play_description),
    Daily("daily", R.string.daily_title, R.string.daily_description),
    TimeAttack("time_attack", R.string.time_attack_title, R.string.time_attack_description),
    Comparison("comparison", R.string.comparison_title, R.string.comparison_description),
    History("history", R.string.history_title, R.string.history_description),
    Settings("settings", R.string.settings_title, R.string.settings_description),
    About("about", R.string.about_title, R.string.about_description),
    ;

    companion object {
        val homeSections = listOf(Play, Daily, TimeAttack, Comparison, History, Settings, About)
    }
}
