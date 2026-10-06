package com.akhil.aimaze.ui.game

import android.content.Context

class GamePreferences(context: Context) {
    private val prefs = context.applicationContext.getSharedPreferences(
        "maze_rush_game_prefs",
        Context.MODE_PRIVATE,
    )

    var soundEnabled: Boolean
        get() = prefs.getBoolean(KEY_SOUND, true)
        set(value) = prefs.edit().putBoolean(KEY_SOUND, value).apply()

    var hapticsEnabled: Boolean
        get() = prefs.getBoolean(KEY_HAPTICS, true)
        set(value) = prefs.edit().putBoolean(KEY_HAPTICS, value).apply()

    companion object {
        private const val KEY_SOUND = "sound_enabled"
        private const val KEY_HAPTICS = "haptics_enabled"
    }
}
