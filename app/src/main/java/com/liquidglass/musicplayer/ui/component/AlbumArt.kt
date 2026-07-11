package com.liquidglass.musicplayer.ui.component

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.MusicOff
import androidx.compose.material3.Icon
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.palette.graphics.Palette
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.liquidglass.musicplayer.ui.theme.*

@Composable
fun AlbumArt(
    url: String,
    modifier: Modifier = Modifier,
    size: Dp = 200.dp,
    cornerRadius: Dp = 12.dp,
    showShadow: Boolean = true
) {
    val shape = RoundedCornerShape(cornerRadius)

    if (url.isNotBlank()) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(url)
                .crossfade(true)
                .build(),
            contentDescription = "Album Art",
            contentScale = ContentScale.Crop,
            modifier = modifier
                .size(size)
                .then(
                    if (showShadow) {
                        Modifier.shadow(
                            elevation = 16.dp,
                            shape = shape,
                            ambientColor = Color.Black.copy(alpha = 0.5f),
                            spotColor = Color.Black.copy(alpha = 0.3f)
                        )
                    } else Modifier
                )
                .clip(shape)
        )
    } else {
        Box(
            modifier = modifier
                .size(size)
                .clip(shape)
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            SurfaceVariant,
                            SurfaceElevated
                        )
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.MusicNote,
                contentDescription = null,
                tint = OnSurfaceVariant.copy(alpha = 0.5f),
                modifier = Modifier.size(size * 0.4f)
            )
        }
    }
}

@Composable
fun AlbumArtSmall(
    url: String,
    modifier: Modifier = Modifier,
    size: Dp = 48.dp
) {
    val shape = RoundedCornerShape(8.dp)

    if (url.isNotBlank()) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(url)
                .crossfade(true)
                .build(),
            contentDescription = "Album Art",
            contentScale = ContentScale.Crop,
            modifier = modifier
                .size(size)
                .clip(shape)
        )
    } else {
        Box(
            modifier = modifier
                .size(size)
                .clip(shape)
                .background(SurfaceVariant),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.MusicNote,
                contentDescription = null,
                tint = OnSurfaceVariant.copy(alpha = 0.5f),
                modifier = Modifier.size(size * 0.5f)
            )
        }
    }
}

@Composable
fun ExtractedColorBackground(
    imageUrl: String,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    var dominantColor by remember { mutableStateOf(Surface) }
    var mutedColor by remember { mutableStateOf(SurfaceVariant) }

    Box(
        modifier = modifier
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        dominantColor.copy(alpha = 0.4f),
                        mutedColor.copy(alpha = 0.2f),
                        Color.Black
                    )
                )
            )
    ) {
        content()
    }
}

fun extractColorsFromBitmap(bitmap: Bitmap): Pair<Color, Color> {
    val palette = Palette.from(bitmap).generate()
    val dominant = palette.getDominantSwatch()?.rgb?.let { Color(it) } ?: Surface
    val muted = palette.getMutedSwatch()?.rgb?.let { Color(it) } ?: SurfaceVariant
    return dominant to muted
}
