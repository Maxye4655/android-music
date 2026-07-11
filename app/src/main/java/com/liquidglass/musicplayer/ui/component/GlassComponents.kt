package com.liquidglass.musicplayer.ui.component

import android.os.Build
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.liquidglass.musicplayer.ui.theme.*
import io.github.kyant0.backdrop.Backdrop
import io.github.kyant0.backdrop.EffectsScope
import io.github.kyant0.backdrop.Highlight
import io.github.kyant0.backdrop.Shadow
import io.github.kyant0.backdrop.backdrop
import io.github.kyant0.backdrop.effect.blur
import io.github.kyant0.backdrop.effect.lens
import io.github.kyant0.backdrop.effect.vibrancy
import io.github.kyant0.backdrop.highlight.HighlightStyle
import io.github.kyant0.backdrop.highlight.PlainHighlightStyle
import io.github.kyant0.backdrop.highlight.HighlightAmbientStyle
import io.github.kyant0.backdrop.rememberLayerBackdrop
import kotlin.math.min

@Composable
fun LiquidGlassCard(
    modifier: Modifier = Modifier,
    backdrop: Backdrop,
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

    Column(
        modifier = modifier
            .graphicsLayer {
                scaleX = pressScale
                scaleY = pressScale
            }
            .backdrop(
                backdrop = backdrop,
                shape = { RoundedCornerShape(cornerRadius.toPx()) },
                effects = {
                    vibrancy()
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                        blur(if (isPressed) 2f.dp.toPx() else 6f.dp.toPx())
                    }
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        lens(
                            refractionHeight = cornerRadius.toPx(),
                            refractionAmount = if (isPressed) 24f.dp.toPx() else 16f.dp.toPx()
                        )
                    }
                },
                highlight = {
                    Highlight.Default(
                        style = PlainHighlightStyle(
                            alpha = if (isPressed) 0.7f else 0.4f
                        )
                    )
                },
                shadow = {
                    Shadow(
                        elevation = if (isPressed) 16f.dp.toPx() else 8f.dp.toPx(),
                        ambientColor = Color.Black.copy(alpha = 0.4f),
                        spotColor = Color.Black.copy(alpha = 0.2f)
                    )
                }
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
    backdrop: Backdrop,
    cornerRadius: Dp = 16.dp,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .backdrop(
                backdrop = backdrop,
                shape = { RoundedCornerShape(cornerRadius.toPx()) },
                effects = {
                    vibrancy()
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                        blur(4f.dp.toPx())
                    }
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        lens(
                            refractionHeight = cornerRadius.toPx(),
                            refractionAmount = 12f.dp.toPx()
                        )
                    }
                },
                highlight = {
                    Highlight.Default(
                        style = PlainHighlightStyle(alpha = 0.3f)
                    )
                },
                shadow = {
                    Shadow(elevation = 4f.dp.toPx())
                }
            )
    ) {
        content()
    }
}

@Composable
fun LiquidGlassButton(
    modifier: Modifier = Modifier,
    backdrop: Backdrop,
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

    Row(
        modifier = modifier
            .graphicsLayer {
                scaleX = pressScale
                scaleY = pressScale
            }
            .backdrop(
                backdrop = backdrop,
                shape = { RoundedCornerShape(cornerRadius.toPx()) },
                effects = {
                    vibrancy()
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                        blur(if (isPressed) 0f else 3f.dp.toPx())
                    }
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        lens(
                            refractionHeight = cornerRadius.toPx() * 0.6f,
                            refractionAmount = if (isPressed) 20f.dp.toPx() else 8f.dp.toPx()
                        )
                    }
                },
                highlight = {
                    Highlight.Default(
                        style = PlainHighlightStyle(alpha = if (isPressed) 0.6f else 0.35f)
                    )
                },
                shadow = {
                    Shadow(
                        elevation = if (isPressed) 12f.dp.toPx() else 6f.dp.toPx()
                    )
                }
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
    backdrop: Backdrop,
    content: @Composable RowScope.() -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .backdrop(
                backdrop = backdrop,
                shape = {
                    RoundRect(
                        left = 0f,
                        top = 0f,
                        right = size.width,
                        bottom = size.height,
                        topLeftCornerRadius = CornerRadius(24f.dp.toPx()),
                        topRightCornerRadius = CornerRadius(24f.dp.toPx()),
                        bottomLeftCornerRadius = CornerRadius(0f),
                        bottomRightCornerRadius = CornerRadius(0f)
                    )
                },
                effects = {
                    vibrancy()
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                        blur(8f.dp.toPx())
                    }
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        lens(
                            refractionHeight = 12f.dp.toPx(),
                            refractionAmount = 8f.dp.toPx()
                        )
                    }
                },
                highlight = {
                    Highlight.Default(
                        style = PlainHighlightStyle(alpha = 0.3f)
                    )
                },
                shadow = {
                    Shadow(
                        elevation = 16f.dp.toPx(),
                        ambientColor = Color.Black.copy(alpha = 0.5f),
                        spotColor = Color.Black.copy(alpha = 0.3f)
                    )
                },
                onDrawSurface = {
                    drawRect(Color.Black.copy(alpha = 0.55f))
                }
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
    backdrop: Backdrop,
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

    Box(
        modifier = modifier
            .graphicsLayer {
                scaleX = pressScale
                scaleY = pressScale
            }
            .backdrop(
                backdrop = backdrop,
                shape = { RoundedCornerShape(16f.dp.toPx()) },
                effects = {
                    vibrancy()
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                        blur(if (isPressed) 2f.dp.toPx() else 6f.dp.toPx())
                    }
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        lens(
                            refractionHeight = 10f.dp.toPx(),
                            refractionAmount = if (isPressed) 16f.dp.toPx() else 10f.dp.toPx()
                        )
                    }
                },
                highlight = {
                    Highlight.Default(
                        style = PlainHighlightStyle(alpha = 0.35f)
                    )
                },
                shadow = {
                    Shadow(elevation = 8f.dp.toPx())
                }
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
    backdrop: Backdrop,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .backdrop(
                backdrop = backdrop,
                shape = { RoundedCornerShape(14f.dp.toPx()) },
                effects = {
                    vibrancy()
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                        blur(3f.dp.toPx())
                    }
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        lens(
                            refractionHeight = 8f.dp.toPx(),
                            refractionAmount = 6f.dp.toPx()
                        )
                    }
                },
                highlight = {
                    Highlight.Default(
                        style = PlainHighlightStyle(alpha = 0.25f)
                    )
                },
                shadow = {
                    Shadow(elevation = 4f.dp.toPx())
                }
            )
    ) {
        content()
    }
}
