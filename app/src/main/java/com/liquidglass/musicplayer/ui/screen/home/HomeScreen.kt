package com.liquidglass.musicplayer.ui.screen.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.liquidglass.musicplayer.data.model.Album
import com.liquidglass.musicplayer.data.model.Playlist
import com.liquidglass.musicplayer.data.model.Track
import com.liquidglass.musicplayer.ui.component.*
import com.liquidglass.musicplayer.ui.theme.*

@Composable
fun HomeScreen(
    onTrackClick: (Track) -> Unit,
    onPlaylistClick: (Playlist) -> Unit,
    onAlbumClick: (Album) -> Unit,
    onNavigateToLogin: () -> Unit = {},
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 100.dp)
    ) {
        // Header
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Primary.copy(alpha = 0.15f),
                                Color.Transparent
                            )
                        )
                    )
                    .statusBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 24.dp)
            ) {
                Column {
                    Text(
                        text = "Good ${getGreetingTime()}",
                        style = MaterialTheme.typography.displaySmall,
                        color = OnBackground
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "What do you want to listen to?",
                        style = MaterialTheme.typography.bodyLarge,
                        color = OnSurfaceVariant
                    )
                }
            }
        }

        // Quick Picks grid
        if (uiState.recentlyPlayed.isNotEmpty()) {
            item {
                SectionHeader(title = "Recently Played")
            }
            item {
                QuickPicksGrid(
                    tracks = uiState.recentlyPlayed.take(6),
                    onTrackClick = onTrackClick
                )
            }
        }

        // Top Tracks
        if (uiState.topTracks.isNotEmpty()) {
            item {
                SectionHeader(title = "Your Top Songs")
            }
            item {
                HorizontalTrackList(
                    tracks = uiState.topTracks,
                    onTrackClick = onTrackClick
                )
            }
        }

        // Playlists
        if (uiState.playlists.isNotEmpty()) {
            item {
                SectionHeader(title = "Your Playlists")
            }
            item {
                HorizontalPlaylistList(
                    playlists = uiState.playlists,
                    onPlaylistClick = onPlaylistClick
                )
            }
        }

        // Recommendations
        if (uiState.recommendations.isNotEmpty()) {
            item {
                SectionHeader(title = "Recommended For You")
            }
            item {
                HorizontalTrackList(
                    tracks = uiState.recommendations,
                    onTrackClick = onTrackClick
                )
            }
        }

        // Loading
        if (uiState.isLoading) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Primary)
                }
            }
        }

        // Connect Spotify prompt
        if (!uiState.isAuthenticated) {
            item {
                ConnectSpotifyCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    onClick = onNavigateToLogin
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(AccentGreen),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.MusicNote,
                                contentDescription = null,
                                tint = Color.White
                            )
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Connect Spotify",
                                style = MaterialTheme.typography.titleMedium,
                                color = OnBackground
                            )
                            Text(
                                text = "Link your account for the full experience",
                                style = MaterialTheme.typography.bodySmall,
                                color = OnSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun QuickPicksGrid(
    tracks: List<Track>,
    onTrackClick: (Track) -> Unit
) {
    Column(
        modifier = Modifier.padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        tracks.chunked(2).forEach { row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                row.forEach { track ->
                    GlassSurface(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { onTrackClick(track) }
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            AlbumArtSmall(
                                url = track.albumArtUrl,
                                size = 48.dp
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = track.name,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = OnSurface,
                                    maxLines = 1
                                )
                                Text(
                                    text = track.artist,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = OnSurfaceVariant,
                                    maxLines = 1
                                )
                            }
                        }
                    }
                }
                // Fill empty spots
                repeat(2 - row.size) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
private fun HorizontalPlaylistList(
    playlists: List<Playlist>,
    onPlaylistClick: (Playlist) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        playlists.take(5).forEach { playlist ->
            Column(
                modifier = Modifier
                    .width(140.dp)
                    .clickable { onPlaylistClick(playlist) },
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                AlbumArt(
                    url = playlist.coverUrl,
                    size = 140.dp,
                    cornerRadius = 8.dp
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = playlist.name,
                    style = MaterialTheme.typography.bodySmall,
                    color = OnSurface,
                    maxLines = 1,
                    modifier = Modifier.fillMaxWidth()
                )
                Text(
                    text = "${playlist.trackCount} songs",
                    style = MaterialTheme.typography.labelSmall,
                    color = OnSurfaceVariant,
                    maxLines = 1,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

private fun getGreetingTime(): String {
    val hour = java.util.Calendar.getInstance().get(java.util.Calendar.HOUR_OF_DAY)
    return when {
        hour < 12 -> "Morning"
        hour < 17 -> "Afternoon"
        else -> "Evening"
    }
}
