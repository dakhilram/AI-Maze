package com.akhil.aimaze.ui.game

import androidx.annotation.DrawableRes
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.akhil.aimaze.R

enum class GameBackdropStyle(@DrawableRes val drawableRes: Int) {
    Home(R.drawable.bg_home_art),
    Gameplay(R.drawable.bg_game_art),
}

@Composable
fun GameBackdrop(
    style: GameBackdropStyle,
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit,
) {
    val motion = rememberInfiniteTransition(label = "backgroundMotion")
    val drift by motion.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(7_000),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "backgroundDrift",
    )

    Box(modifier = modifier.fillMaxSize()) {
        Image(
            painter = painterResource(style.drawableRes),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
        )

        Canvas(modifier = Modifier.fillMaxSize()) {
            val orangeCenter = Offset(
                x = size.width * (0.10f + drift * 0.12f),
                y = size.height * (0.26f + drift * 0.06f),
            )
            val blueCenter = Offset(
                x = size.width * (0.90f - drift * 0.12f),
                y = size.height * (0.52f - drift * 0.08f),
            )

            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0x2EFF8A1F),
                        Color.Transparent,
                    ),
                    center = orangeCenter,
                    radius = size.minDimension * 0.70f,
                ),
                radius = size.minDimension * 0.70f,
                center = orangeCenter,
            )
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0x224F7CFF),
                        Color.Transparent,
                    ),
                    center = blueCenter,
                    radius = size.minDimension * 0.82f,
                ),
                radius = size.minDimension * 0.82f,
                center = blueCenter,
            )
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0x16000000),
                            Color(0x43000000),
                            Color(0xCB000000),
                        ),
                    ),
                ),
        )

        content()
    }
}
