package com.liquidglass.musicplayer.ui.component

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.liquidglass.musicplayer.ui.theme.GlassBorder
import com.liquidglass.musicplayer.ui.theme.GlassBorderLight

private val GlassHighlight = Brush.verticalGradient(
    colors = listOf(
        Color.White.copy(alpha = 0.15f),
        Color.White.copy(alpha = 0.05f),
        Color.Transparent
    ),
    startY = 0f,
    endY = 300f
)

private val GlassTint = Brush.verticalGradient(
    colors = listOf(
        Color(0x30FFFFFF),
        Color(0x18FFFFFF)
    )
)

private fun glassBackground(alpha: Float = 0.12f) = Brush.verticalGradient(
    colors = listOf(
        Color.White.copy(alpha = alpha + 0.04f),
        Color.White.copy(alpha = alpha),
        Color.White.copy(alpha = alpha - 0.02f)
    )
)

@Composable
fun GlassSurface(
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 16.dp,
    content: @Composable () -> Unit
) = LiquidGlassSurface(modifier = modifier, cornerRadius = cornerRadius, content = content)

@Composable
fun LiquidGlassCard(
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 20.dp,
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    var isPressed by remember { mutableStateOf(false) }
    val pressScale by animateFloatAsState(
        targetValue = if (isPressed) 0.97f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessHigh
        ),
        label = "press_scale"
    )
    val shape = RoundedCornerShape(cornerRadius)
    val borderAlpha by animateFloatAsState(
        targetValue = if (isPressed) 0.35f else 0.2f,
        label = "border_alpha"
    )

    Column(
        modifier = modifier
            .graphicsLayer {
                scaleX = pressScale
                scaleY = pressScale
            }
            .shadow(
                elevation = if (isPressed) 16.dp else 8.dp,
                shape = shape,
                ambientColor = Color.Black.copy(alpha = 0.4f),
                spotColor = Color.Black.copy(alpha = 0.2f)
            )
            .clip(shape)
            .background(glassBackground())
            .background(brush = GlassTint)
            .background(brush = GlassHighlight)
            .border(
                width = 0.5.dp,
                brush = Brush.linearGradient(
                    colors = listOf(
                        GlassBorderLight.copy(alpha = borderAlpha),
                        GlassBorder.copy(alpha = borderAlpha * 0.5f)
                    )
                ),
                shape = shape
            )
            .then(
                if (onClick != null) {
                    Modifier.pointerInput(Unit) {
                        detectTapGestures(
                            onPress = {
                                isPressed = true
                                tryAwaitRelease()
                                isPressed = false
                            },
                            onTap = { onClick() }
                        )
                    }
                } else Modifier
            )
            .padding(16.dp),
        content = content
    )
}

@Composable
fun LiquidGlassSurface(
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 16.dp,
    content: @Composable () -> Unit
) {
    val shape = RoundedCornerShape(cornerRadius)
    Box(
        modifier = modifier
            .shadow(
                elevation = 4.dp,
                shape = shape,
                ambientColor = Color.Black.copy(alpha = 0.3f),
                spotColor = Color.Black.copy(alpha = 0.15f)
            )
            .clip(shape)
            .background(glassBackground(0.10f))
            .background(brush = GlassTint)
            .background(brush = GlassHighlight)
            .border(
                width = 0.5.dp,
                brush = Brush.linearGradient(
                    colors = listOf(GlassBorderLight, GlassBorder)
                ),
                shape = shape
            )
    ) {
        content()
    }
}

@Composable
fun LiquidGlassButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    cornerRadius: Dp = 28.dp,
    content: @Composable RowScope.() -> Unit
) {
    var isPressed by remember { mutableStateOf(false) }
    val pressScale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessHigh
        ),
        label = "btn_scale"
    )
    val shape = RoundedCornerShape(cornerRadius)
    val borderAlpha by animateFloatAsState(
        targetValue = if (isPressed) 0.4f else 0.25f,
        label = "btn_border_alpha"
    )

    Row(
        modifier = modifier
            .graphicsLayer {
                scaleX = pressScale
                scaleY = pressScale
            }
            .shadow(
                elevation = if (isPressed) 12.dp else 6.dp,
                shape = shape
            )
            .clip(shape)
            .background(glassBackground(0.14f))
            .background(brush = GlassTint)
            .background(brush = GlassHighlight)
            .border(
                width = 0.5.dp,
                brush = Brush.linearGradient(
                    colors = listOf(
                        GlassBorderLight.copy(alpha = borderAlpha),
                        GlassBorder.copy(alpha = borderAlpha * 0.5f)
                    )
                ),
                shape = shape
            )
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = {
                        isPressed = true
                        tryAwaitRelease()
                        isPressed = false
                    },
                    onTap = { onClick() }
                )
            }
            .padding(horizontal = 24.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
        content = content
    )
}

@Composable
fun LiquidGlassBottomBar(
    modifier: Modifier = Modifier,
    content: @Composable RowScope.() -> Unit
) {
    val shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    Row(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = 16.dp,
                shape = shape,
                ambientColor = Color.Black.copy(alpha = 0.5f),
                spotColor = Color.Black.copy(alpha = 0.3f)
            )
            .clip(shape)
            .background(Color(0xCC0A0A0F))
            .background(brush = GlassTint)
            .background(brush = GlassHighlight)
            .border(
                width = 0.5.dp,
                brush = Brush.verticalGradient(
                    colors = listOf(GlassBorderLight, Color.Transparent)
                ),
                shape = shape
            )
            .padding(horizontal = 24.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically,
        content = content
    )
}

@Composable
fun LiquidGlassMiniPlayer(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    content: @Composable () -> Unit
) {
    var isPressed by remember { mutableStateOf(false) }
    val pressScale by animateFloatAsState(
        targetValue = if (isPressed) 0.98f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessHigh
        ),
        label = "mini_scale"
    )
    val shape = RoundedCornerShape(16.dp)

    Box(
        modifier = modifier
            .graphicsLayer {
                scaleX = pressScale
                scaleY = pressScale
            }
            .shadow(
                elevation = 8.dp,
                shape = shape
            )
            .clip(shape)
            .background(glassBackground(0.12f))
            .background(brush = GlassTint)
            .background(brush = GlassHighlight)
            .border(
                width = 0.5.dp,
                brush = Brush.linearGradient(
                    colors = listOf(GlassBorderLight, GlassBorder)
                ),
                shape = shape
            )
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = {
                        isPressed = true
                        tryAwaitRelease()
                        isPressed = false
                    },
                    onTap = { onClick() }
                )
            }
    ) {
        content()
    }
}

@Composable
fun LiquidGlassSearchBar(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val shape = RoundedCornerShape(14.dp)
    Box(
        modifier = modifier
            .shadow(
                elevation = 4.dp,
                shape = shape
            )
            .clip(shape)
            .background(glassBackground(0.10f))
            .background(brush = GlassTint)
            .background(brush = GlassHighlight)
            .border(
                width = 0.5.dp,
                brush = Brush.linearGradient(
                    colors = listOf(GlassBorderLight, GlassBorder)
                ),
                shape = shape
            )
    ) {
        content()
    }
}

@Composable
fun ConnectSpotifyCard(
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
    content: @Composable () -> Unit
) {
    val shape = RoundedCornerShape(20.dp)
    Box(
        modifier = modifier
            .shadow(
                elevation = 8.dp,
                shape = shape,
                ambientColor = Color.Black.copy(alpha = 0.4f),
                spotColor = Color.Black.copy(alpha = 0.2f)
            )
            .clip(shape)
            .background(glassBackground())
            .background(brush = GlassTint)
            .background(brush = GlassHighlight)
            .border(
                width = 0.5.dp,
                brush = Brush.linearGradient(
                    colors = listOf(GlassBorderLight, GlassBorder)
                ),
                shape = shape
            )
            .clickable(onClick = onClick)
            .padding(16.dp)
    ) {
        content()
    }
}
