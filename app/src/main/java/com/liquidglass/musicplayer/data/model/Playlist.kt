package com.liquidglass.musicplayer.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "playlists")
data class Playlist(
    @PrimaryKey val id: String,
    val name: String,
    val description: String,
    val ownerName: String,
    val ownerUrl: String?,
    val coverUrl: String,
    val coverUrlLarge: String,
    val trackCount: Int,
    val isOwn: Boolean = false,
    val isDownloaded: Boolean = false,
    val lastSyncedAt: Long = 0L
)
