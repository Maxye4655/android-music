package com.liquidglass.musicplayer.ui.screen.stats

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.liquidglass.musicplayer.ui.component.*
import com.liquidglass.musicplayer.ui.theme.*

@Composable
fun StatsScreen(
    onTrackClick: (com.liquidglass.musicplayer.data.model.Track) -> Unit = {},
    viewModel: StatsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 120.dp)
    ) {
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        brush = androidx.compose.ui.graphics.Brush.verticalGradient(
                            colors = listOf(
                                Primary.copy(alpha = 0.12f),
                                Color.Transparent
                            )
                        )
                    )
                    .statusBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 24.dp)
            ) {
                Column {
                    Text(
                        text = "Your Stats",
                        style = MaterialTheme.typography.displaySmall,
                        fontWeight = FontWeight.Bold,
                        color = OnBackground
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "See how you listen",
                        style = MaterialTheme.typography.bodyLarge,
                        color = OnSurfaceVariant
                    )
                }
            }
        }

        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatCard(
                    modifier = Modifier.weight(1f),
                    value = "${uiState.totalPlayCount}",
                    label = "Plays",
                    icon = Icons.Default.PlayArrow
                )
                StatCard(
                    modifier = Modifier.weight(1f),
                    value = formatListeningTime(uiState.totalListeningTimeMs),
                    label = "Listened",
                    icon = Icons.Default.Schedule
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatCard(
                    modifier = Modifier.weight(1f),
                    value = "${uiState.uniqueArtistCount}",
                    label = "Artists",
                    icon = Icons.Default.Person
                )
                StatCard(
                    modifier = Modifier.weight(1f),
                    value = "${uiState.totalTrackCount}",
                    label = "Tracks",
                    icon = Icons.Default.MusicNote
                )
            }
        }

        if (uiState.topTracks.isNotEmpty()) {
            item {
                Spacer(modifier = Modifier.height(28.dp))
                SectionHeader(title = "Top Songs")
            }
            itemsIndexed(uiState.topTracks) { index, track ->
                AnimatedStatItem(index = index) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onTrackClick(track) }
                            .padding(horizontal = 16.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "${index + 1}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = if (index < 3) Primary else OnSurfaceVariant,
                            modifier = Modifier.width(32.dp)
                        )
                        AlbumArtSmall(
                            url = track.albumArtUrl,
                            size = 44.dp
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = track.name,
                                style = MaterialTheme.typography.bodyMedium,
                                color = OnSurface,
                                maxLines = 1
                            )
                            Text(
                                text = track.artist,
                                style = MaterialTheme.typography.bodySmall,
                                color = OnSurfaceVariant,
                                maxLines = 1
                            )
                        }
                        Text(
                            text = "${track.playCount} plays",
                            style = MaterialTheme.typography.labelMedium,
                            color = Primary.copy(alpha = 0.8f)
                        )
                    }
                }
            }
        }

        if (uiState.topArtists.isNotEmpty()) {
            item {
                Spacer(modifier = Modifier.height(24.dp))
                SectionHeader(title = "Top Artists")
            }
            item {
                HorizontalArtistStats(artists = uiState.topArtists)
            }
        }

        if (uiState.totalPlayCount > 0) {
            item {
                Spacer(modifier = Modifier.height(24.dp))
                ListeningChart(
                    topArtists = uiState.topArtists,
                    totalPlays = uiState.totalPlayCount
                )
            }
        }
    }
}

@Composable
private fun StatCard(
    modifier: Modifier = Modifier,
    value: String,
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    val shape = RoundedCornerShape(16.dp)
    LiquidGlassCard(
        modifier = modifier,
        cornerRadius = 16.dp
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Primary.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = Primary,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = value,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = OnBackground
                )
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelMedium,
                    color = OnSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun AnimatedStatItem(
    index: Int,
    content: @Composable () -> Unit
) {
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(index * 60L)
        visible = true
    }

    androidx.compose.animation.AnimatedVisibility(
        visible = visible,
        enter = androidx.compose.animation.fadeIn(
            animationSpec = tween(durationMillis = 300)
        ) + androidx.compose.animation.slideInVertically(
            initialOffsetY = { it / 4 },
            animationSpec = tween(durationMillis = 300)
        )
    ) {
        content()
    }
}

@Composable
private fun HorizontalArtistStats(
    artists: List<com.liquidglass.musicplayer.data.local.ArtistPlayCount>
) {
    val maxPlays = artists.maxOfOrNull { it.playCount } ?: 1

    Column(
        modifier = Modifier.padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        artists.take(5).forEach { artist ->
            val fraction = artist.playCount.toFloat() / maxPlays.toFloat()
            var animFraction by remember { mutableFloatStateOf(0f) }
            LaunchedEffect(Unit) {
                kotlinx.coroutines.delay(100)
                animFraction = fraction
            }
            val animatedFraction by animateFloatAsState(
                targetValue = animFraction,
                animationSpec = tween(durationMillis = 800, easing = EaseOutCubic),
                label = "bar"
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = artist.artist,
                    style = MaterialTheme.typography.bodySmall,
                    color = OnSurface,
                    modifier = Modifier.width(100.dp),
                    maxLines = 1
                )
                Spacer(modifier = Modifier.width(8.dp))
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(20.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(SurfaceVariant)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .fillMaxWidth(fraction = animatedFraction)
                            .clip(RoundedCornerShape(10.dp))
                            .background(
                                brush = androidx.compose.ui.graphics.Brush.horizontalGradient(
                                    colors = listOf(Primary, Secondary)
                                )
                            )
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "${artist.playCount}",
                    style = MaterialTheme.typography.labelSmall,
                    color = OnSurfaceVariant,
                    modifier = Modifier.width(36.dp)
                )
            }
        }
    }
}

@Composable
private fun ListeningChart(
    topArtists: List<com.liquidglass.musicplayer.data.local.ArtistPlayCount>,
    totalPlays: Int
) {
    if (totalPlays == 0 || topArtists.isEmpty()) return

    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        Text(
            text = "Listening Breakdown",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = OnBackground,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        LiquidGlassCard(cornerRadius = 16.dp) {
            Column {
                val otherPlays = totalPlays - topArtists.take(5).sumOf { it.playCount }
                val slices = topArtists.take(5).map { it.artist to it.playCount }
                    .let { list -> if (otherPlays > 0) list + ("Others" to otherPlays) else list }

                val colors = listOf(Primary, Secondary, AccentPurple, AccentBlue, AccentPink, OnSurfaceVariant)
                val total = slices.sumOf { it.second }.toFloat()

                Canvas(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                ) {
                    val barWidth = size.width / (slices.size * 2f)
                    val maxBarHeight = size.height - 40f

                    slices.forEachIndexed { index, (name, plays) ->
                        val barHeight = (plays / total) * maxBarHeight
                        val x = index * (barWidth * 2) + barWidth / 2

                        drawRoundRect(
                            color = colors[index % colors.size],
                            topLeft = Offset(x, maxBarHeight - barHeight + 10f),
                            size = Size(barWidth, barHeight),
                            cornerRadius = CornerRadius(barWidth / 3)
                        )

                        drawContext.canvas.nativeCanvas.apply {
                            val paint = android.graphics.Paint().apply {
                                this.color = android.graphics.Color.parseColor("#9A948A")
                                textSize = 24f
                                textAlign = android.graphics.Paint.Align.CENTER
                                isAntiAlias = true
                            }
                            val displayName = if (name.length > 8) name.take(8) + "." else name
                            drawText(
                                displayName,
                                x + barWidth / 2,
                                maxBarHeight + 36f,
                                paint
                            )
                        }
                    }
                }
            }
        }
    }
}

private fun formatListeningTime(ms: Long): String {
    val minutes = ms / (1000L * 60L)
    val hours = minutes / 60
    return if (hours > 0) "${hours}h ${minutes % 60}m" else "${minutes}m"
}

private val EaseOutCubic = CubicBezierEasing(0.33f, 1f, 0.68f, 1f)
