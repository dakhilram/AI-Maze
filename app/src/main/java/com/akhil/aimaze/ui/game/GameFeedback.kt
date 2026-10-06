package com.akhil.aimaze.ui.game

import android.media.AudioManager
import android.media.ToneGenerator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.hapticfeedback.HapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback

class GameFeedback internal constructor(
    private val tones: ToneGenerator,
    private val haptics: HapticFeedback,
) {
    fun move() {
        tones.startTone(ToneGenerator.TONE_PROP_BEEP, 28)
        haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
    }

    fun blocked() {
        tones.startTone(ToneGenerator.TONE_PROP_NACK, 70)
        haptics.performHapticFeedback(HapticFeedbackType.LongPress)
    }

    fun countdown() {
        tones.startTone(ToneGenerator.TONE_PROP_BEEP2, 90)
    }

    fun start() {
        tones.startTone(ToneGenerator.TONE_PROP_ACK, 130)
        haptics.performHapticFeedback(HapticFeedbackType.LongPress)
    }

    fun win() {
        tones.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD, 280)
        haptics.performHapticFeedback(HapticFeedbackType.LongPress)
    }

    fun lose() {
        tones.startTone(ToneGenerator.TONE_PROP_NACK, 220)
        haptics.performHapticFeedback(HapticFeedbackType.LongPress)
    }

    fun release() = tones.release()
}

@Composable
fun rememberGameFeedback(): GameFeedback {
    val haptics = LocalHapticFeedback.current
    val feedback = remember(haptics) {
        GameFeedback(
            tones = ToneGenerator(AudioManager.STREAM_MUSIC, 58),
            haptics = haptics,
        )
    }

    DisposableEffect(feedback) {
        onDispose { feedback.release() }
    }

    return feedback
}
