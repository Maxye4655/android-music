package com.liquidglass.musicplayer.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "albums")
data class Album(
    @PrimaryKey val id: String,
    val name: String,
    val artist: String,
    val artistId: String,
    val coverUrl: String,
    val coverUrlLarge: String,
    val releaseDate: String,
    val totalTracks: Int,
    val isDownloaded: Boolean = false,
    val addedAt: Long = System.currentTimeMillis()
)
