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

    var campaignLevel: Int
        get() = prefs.getInt(KEY_CAMPAIGN_LEVEL, 1).coerceIn(1, CAMPAIGN_LEVELS)
        set(value) = prefs.edit().putInt(KEY_CAMPAIGN_LEVEL, value.coerceIn(1, CAMPAIGN_LEVELS)).apply()

    fun advanceCampaignFrom(level: Int) {
        if (level == campaignLevel && level < CAMPAIGN_LEVELS) {
            campaignLevel = level + 1
        }
    }

    companion object {
        const val CAMPAIGN_LEVELS = 500

        private const val KEY_SOUND = "sound_enabled"
        private const val KEY_HAPTICS = "haptics_enabled"
        private const val KEY_CAMPAIGN_LEVEL = "campaign_level"
    }
}
