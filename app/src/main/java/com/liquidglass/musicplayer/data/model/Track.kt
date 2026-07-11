package com.liquidglass.musicplayer.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tracks")
data class Track(
    @PrimaryKey val id: String,
    val name: String,
    val artist: String,
    val artistId: String,
    val album: String,
    val albumId: String,
    val albumArtUrl: String,
    val albumArtUrlLarge: String,
    val durationMs: Long,
    val previewUrl: String?,
    val uri: String,
    val isExplicit: Boolean = false,
    val popularity: Int = 0,
    val isDownloaded: Boolean = false,
    val downloadPath: String? = null,
    val addedAt: Long = System.currentTimeMillis(),
    val lastPlayedAt: Long = 0L,
    val playCount: Int = 0
) {
    val durationSeconds: Int get() = (durationMs / 1000).toInt()
    val durationFormatted: String
        get() {
            val totalSeconds = durationMs / 1000
            val minutes = totalSeconds / 60
            val seconds = totalSeconds % 60
            return "%d:%02d".format(minutes, seconds)
        }
}
