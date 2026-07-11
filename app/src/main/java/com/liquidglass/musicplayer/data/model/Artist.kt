package com.liquidglass.musicplayer.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "artists")
data class Artist(
    @PrimaryKey val id: String,
    val name: String,
    val imageUrl: String,
    val imageUrlLarge: String,
    val followers: Int = 0,
    val genres: List<String> = emptyList(),
    val isFollowed: Boolean = false
)
