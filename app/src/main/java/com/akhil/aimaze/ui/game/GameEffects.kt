package com.akhil.aimaze.ui.game

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun WinBurst(
    active: Boolean,
    modifier: Modifier = Modifier,
) {
    val progress = remember { Animatable(0f) }
    val particles = remember {
        List(28) { index ->
            val angle = (index / 28f) * 6.2831855f
            val distance = 0.30f + ((index * 37) % 70) / 100f
            angle to distance
        }
    }
    val palette = remember {
        listOf(
            Color(0xFFFF8A1F),
            Color(0xFFFFC857),
            Color(0xFF4F7CFF),
            Color(0xFF57D68D),
            Color(0xFFF5F7FF),
        )
    }

    LaunchedEffect(active) {
        if (active) {
            progress.snapTo(0f)
            progress.animateTo(
                targetValue = 1f,
                animationSpec = tween(900),
            )
        } else {
            progress.snapTo(0f)
        }
    }

    if (active && progress.value < 1f) {
        Canvas(modifier = modifier) {
            val center = Offset(size.width / 2f, size.height * 0.45f)
            particles.forEachIndexed { index, (angle, distanceScale) ->
                val distance = size.minDimension * distanceScale * progress.value
                val gravity = size.height * 0.24f * progress.value * progress.value
                val point = Offset(
                    x = center.x + cos(angle.toDouble()).toFloat() * distance,
                    y = center.y + sin(angle.toDouble()).toFloat() * distance + gravity,
                )
                drawCircle(
                    color = palette[index % palette.size].copy(
                        alpha = (1f - progress.value).coerceIn(0f, 1f),
                    ),
                    radius = (8f - progress.value * 3f).coerceAtLeast(3f),
                    center = point,
                )
            }
        }
    }
}
