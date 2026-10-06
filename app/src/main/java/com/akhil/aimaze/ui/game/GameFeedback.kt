package com.akhil.aimaze.ui.game

import android.media.AudioManager
import android.media.ToneGenerator
import android.os.Handler
import android.os.Looper
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
    private val handler = Handler(Looper.getMainLooper())

    fun button() {
        tones.startTone(ToneGenerator.TONE_PROP_BEEP, 38)
    }

    fun move() {
        tones.startTone(ToneGenerator.TONE_PROP_BEEP2, 30)
        haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
    }

    fun blocked() {
        tones.startTone(ToneGenerator.TONE_PROP_NACK, 85)
        haptics.performHapticFeedback(HapticFeedbackType.LongPress)
    }

    fun countdown() {
        tones.startTone(ToneGenerator.TONE_PROP_BEEP, 95)
    }

    fun start() {
        playSequence(
            ToneGenerator.TONE_PROP_ACK to 90L,
            ToneGenerator.TONE_PROP_BEEP2 to 130L,
        )
        haptics.performHapticFeedback(HapticFeedbackType.LongPress)
    }

    fun star() {
        playSequence(
            ToneGenerator.TONE_PROP_BEEP to 55L,
            ToneGenerator.TONE_PROP_BEEP2 to 75L,
        )
    }

    fun win() {
        playSequence(
            ToneGenerator.TONE_DTMF_1 to 85L,
            ToneGenerator.TONE_DTMF_3 to 85L,
            ToneGenerator.TONE_DTMF_6 to 170L,
        )
        haptics.performHapticFeedback(HapticFeedbackType.LongPress)
    }

    fun lose() {
        playSequence(
            ToneGenerator.TONE_PROP_NACK to 140L,
            ToneGenerator.TONE_SUP_ERROR to 220L,
        )
        haptics.performHapticFeedback(HapticFeedbackType.LongPress)
    }

    private fun playSequence(vararg notes: Pair<Int, Long>) {
        handler.removeCallbacksAndMessages(null)
        var delay = 0L
        notes.forEach { (tone, duration) ->
            handler.postDelayed(
                { tones.startTone(tone, duration.toInt()) },
                delay,
            )
            delay += duration + 22L
        }
    }

    fun release() {
        handler.removeCallbacksAndMessages(null)
        tones.release()
    }
}

@Composable
fun rememberGameFeedback(): GameFeedback {
    val haptics = LocalHapticFeedback.current
    val feedback = remember(haptics) {
        GameFeedback(
            tones = ToneGenerator(AudioManager.STREAM_MUSIC, 72),
            haptics = haptics,
        )
    }

    DisposableEffect(feedback) {
        onDispose { feedback.release() }
    }

    return feedback
}
