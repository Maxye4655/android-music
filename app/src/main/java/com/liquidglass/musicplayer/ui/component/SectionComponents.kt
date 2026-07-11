package com.liquidglass.musicplayer.ui.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.liquidglass.musicplayer.ui.theme.*

@Composable
fun SectionHeader(
    title: String,
    onSeeAll: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = OnBackground
        )

        if (onSeeAll != null) {
            Text(
                text = "See All",
                style = MaterialTheme.typography.labelLarge,
                color = Primary,
                modifier = Modifier.clickable(onClick = onSeeAll)
            )
        }
    }
}

@Composable
fun HorizontalTrackList(
    tracks: List<com.liquidglass.musicplayer.data.model.Track>,
    onTrackClick: (com.liquidglass.musicplayer.data.model.Track) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        tracks.take(6).forEach { track ->
            Column(
                modifier = Modifier
                    .width(140.dp)
                    .clickable { onTrackClick(track) },
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                AlbumArt(
                    url = track.albumArtUrlLarge,
                    size = 140.dp,
                    cornerRadius = 8.dp
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = track.name,
                    style = MaterialTheme.typography.bodySmall,
                    color = OnSurface,
                    maxLines = 1,
                    modifier = Modifier.fillMaxWidth()
                )
                Text(
                    text = track.artist,
                    style = MaterialTheme.typography.labelSmall,
                    color = OnSurfaceVariant,
                    maxLines = 1,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
fun HorizontalAlbumList(
    albums: List<com.liquidglass.musicplayer.data.model.Album>,
    onAlbumClick: (com.liquidglass.musicplayer.data.model.Album) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        albums.take(6).forEach { album ->
            Column(
                modifier = Modifier
                    .width(140.dp)
                    .clickable { onAlbumClick(album) },
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                AlbumArt(
                    url = album.coverUrlLarge,
                    size = 140.dp,
                    cornerRadius = 8.dp
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = album.name,
                    style = MaterialTheme.typography.bodySmall,
                    color = OnSurface,
                    maxLines = 1,
                    modifier = Modifier.fillMaxWidth()
                )
                Text(
                    text = album.artist,
                    style = MaterialTheme.typography.labelSmall,
                    color = OnSurfaceVariant,
                    maxLines = 1,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}
