package com.liquidglass.musicplayer.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.liquidglass.musicplayer.data.model.*

@Database(
    entities = [
        Track::class,
        Playlist::class,
        Album::class,
        Artist::class,
        SearchHistory::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class MusicDatabase : RoomDatabase() {
    abstract fun trackDao(): TrackDao
    abstract fun playlistDao(): PlaylistDao
    abstract fun albumDao(): AlbumDao
    abstract fun artistDao(): ArtistDao
    abstract fun searchHistoryDao(): SearchHistoryDao
}
