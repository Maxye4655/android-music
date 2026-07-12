package com.liquidglass.musicplayer.ui.component

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.*
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.liquidglass.musicplayer.ui.theme.*

@Composable
fun VinylTurntable(
    albumArtUrl: String,
    isPlaying: Boolean,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "vinyl")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 8000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "vinyl_rotation"
    )

    val currentRotation = remember { mutableFloatStateOf(0f) }
    val animatable = remember { Animatable(0f) }

    LaunchedEffect(isPlaying) {
        if (isPlaying) {
            animatable.animateTo(
                targetValue = currentRotation.floatValue + 360f,
                animationSpec = tween(durationMillis = 8000, easing = LinearEasing)
            ) {
                currentRotation.floatValue = this.value % 360f
            }
        } else {
            animatable.stop()
            currentRotation.floatValue = animatable.value
        }
    }

    Box(
        modifier = modifier.size(300.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .shadow(
                    elevation = 24.dp,
                    shape = CircleShape,
                    ambientColor = Color.Black.copy(alpha = 0.6f),
                    spotColor = Color.Black.copy(alpha = 0.4f)
                )
                .clip(CircleShape)
        ) {
            val canvasSize = size.minDimension
            val center = Offset(size.width / 2, size.height / 2)
            val vinylRadius = canvasSize / 2

            drawCircle(color = VinylBlack, radius = vinylRadius)

            val grooveCount = 24
            for (i in 1..grooveCount) {
                val grooveRadius = vinylRadius * (0.35f + (i.toFloat() / grooveCount) * 0.60f)
                val alpha = if (i % 3 == 0) 0.15f else 0.08f
                drawCircle(
                    color = Color.White.copy(alpha = alpha),
                    radius = grooveRadius,
                    center = center,
                    style = Stroke(width = 0.5.dp.toPx())
                )
            }

            drawCircle(
                color = Color(0xFF2A2A2A),
                radius = vinylRadius * 0.35f,
                center = center
            )

            drawCircle(
                color = Color(0xFF222222),
                radius = vinylRadius * 0.34f,
                center = center,
                style = Stroke(width = 0.5.dp.toPx())
            )

            val labelRadius = vinylRadius * 0.28f
            drawCircle(
                color = Color(0xFF1A1A1A),
                radius = labelRadius,
                center = center
            )

            drawCircle(
                color = Color(0xFF0F0F0F),
                radius = vinylRadius * 0.04f,
                center = center
            )

            drawCircle(
                color = Color(0xFF333333),
                radius = vinylRadius * 0.02f,
                center = center
            )
        }

        Box(
            modifier = Modifier
                .size(148.dp)
                .graphicsLayer {
                    rotationZ = animatable.value
                }
                .clip(CircleShape),
            contentAlignment = Alignment.Center
        ) {
            AsyncImage(
                model = albumArtUrl,
                contentDescription = null,
                modifier = Modifier
                    .size(148.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )

            Canvas(modifier = Modifier.size(148.dp)) {
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color.Black.copy(alpha = 0.15f)
                        ),
                        center = center,
                        radius = size.minDimension / 2
                    ),
                    radius = size.minDimension / 2
                )
            }

            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(VinylBlack)
            )
        }
    }
}
