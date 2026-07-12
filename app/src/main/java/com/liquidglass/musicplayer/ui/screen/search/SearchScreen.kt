package com.liquidglass.musicplayer.ui.screen.search

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    onTrackClick: (Track) -> Unit,
    onPlaylistClick: (Playlist) -> Unit,
    onAlbumClick: (Album) -> Unit,
    viewModel: SearchViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
    ) {
        // Search Bar
        LiquidGlassSearchBar(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            TextField(
                value = uiState.query,
                onValueChange = { viewModel.onQueryChange(it) },
                modifier = Modifier.fillMaxWidth(),
                placeholder = {
                    Text(
                        text = "Artists, songs, or albums",
                        color = OnSurfaceVariant
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = OnSurfaceVariant
                    )
                },
                trailingIcon = {
                    if (uiState.query.isNotEmpty()) {
                        IconButton(onClick = { viewModel.clearQuery() }) {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = "Clear",
                                tint = OnSurfaceVariant
                            )
                        }
                    }
                },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    cursorColor = Primary
                ),
                singleLine = true
            )
        }

        // Content
        AnimatedContent(
            targetState = uiState.showResults,
            label = "search_content"
        ) { showResults ->
            if (showResults) {
                SearchResults(
                    uiState = uiState,
                    onTrackClick = onTrackClick,
                    onAlbumClick = onAlbumClick,
                    onPlaylistClick = onPlaylistClick
                )
            } else {
                SearchBrowse(
                    onSearchClick = { viewModel.onQueryChange(it) }
                )
            }
        }
    }
}

@Composable
private fun SearchResults(
    uiState: SearchUiState,
    onTrackClick: (Track) -> Unit,
    onAlbumClick: (Album) -> Unit,
    onPlaylistClick: (Playlist) -> Unit
) {
    if (uiState.isLoading) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = Primary)
        }
        return
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 100.dp)
    ) {
        // Tracks
        if (uiState.tracks.isNotEmpty()) {
            item {
                SectionHeader(title = "Songs")
            }
            val tracksToShow = uiState.tracks.take(10)
            items(tracksToShow.size) { idx ->
                TrackListItem(
                    track = tracksToShow[idx],
                    onClick = { onTrackClick(tracksToShow[idx]) }
                )
            }
        }

        // Albums
        if (uiState.albums.isNotEmpty()) {
            item {
                SectionHeader(title = "Albums")
            }
            item {
                HorizontalAlbumList(
                    albums = uiState.albums,
                    onAlbumClick = onAlbumClick
                )
            }
        }

        // No results
        if (uiState.tracks.isEmpty() && uiState.albums.isEmpty() && !uiState.isLoading) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.SearchOff,
                            contentDescription = null,
                            tint = OnSurfaceVariant.copy(alpha = 0.5f),
                            modifier = Modifier.size(64.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "No results found",
                            style = MaterialTheme.typography.bodyLarge,
                            color = OnSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SearchBrowse(
    onSearchClick: (String) -> Unit
) {
    val categories = listOf(
        "Pop" to Primary,
        "Hip-Hop" to AccentOrange,
        "Rock" to AccentPurple,
        "R&B" to AccentBlue,
        "Latin" to AccentGreen,
        "Podcasts" to AccentPink,
        "Charts" to AccentOrange,
        "Mood" to AccentPurple,
        "Workout" to AccentGreen,
        "Party" to Primary,
        "Indie" to AccentBlue,
        "Jazz" to AccentPink
    )

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 100.dp)
    ) {
        item {
            Text(
                text = "Browse All",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = OnBackground,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp)
            )
        }

        val chunkedCategories = categories.chunked(2)
        items(chunkedCategories.size) { idx ->
            val row = chunkedCategories[idx]
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                for ((name, color) in row) {
                    GlassSurface(
                        modifier = Modifier
                            .weight(1f)
                            .height(100.dp)
                            .clickable { onSearchClick(name) },
                        cornerRadius = 12.dp
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    brush = Brush.horizontalGradient(
                                        colors = listOf(
                                            color.copy(alpha = 0.4f),
                                            color.copy(alpha = 0.15f)
                                        )
                                    )
                                )
                        ) {
                            Text(
                                text = name,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                modifier = Modifier.padding(16.dp)
                            )
                        }
                    }
                }
                if (row.size < 2) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}
