package com.liquidglass.musicplayer.data.local

import androidx.room.*
import com.liquidglass.musicplayer.data.model.Track
import kotlinx.coroutines.flow.Flow

@Dao
interface TrackDao {

    @Query("SELECT * FROM tracks ORDER BY lastPlayedAt DESC")
    fun getRecentlyPlayed(): Flow<List<Track>>

    @Query("SELECT * FROM tracks WHERE isDownloaded = 1 ORDER BY name ASC")
    fun getDownloadedTracks(): Flow<List<Track>>

    @Query("SELECT * FROM tracks ORDER BY playCount DESC LIMIT :limit")
    fun getTopTracks(limit: Int = 20): Flow<List<Track>>

    @Query("SELECT * FROM tracks WHERE id = :id")
    suspend fun getTrack(id: String): Track?

    @Query("SELECT * FROM tracks WHERE name LIKE '%' || :query || '%' OR artist LIKE '%' || :query || '%' OR album LIKE '%' || :query || '%'")
    fun searchLocal(query: String): Flow<List<Track>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(track: Track)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(tracks: List<Track>)

    @Update
    suspend fun update(track: Track)

    @Delete
    suspend fun delete(track: Track)

    @Query("DELETE FROM tracks WHERE id = :id")
    suspend fun deleteById(id: String)

    @Query("UPDATE tracks SET isDownloaded = 1, downloadPath = :path WHERE id = :id")
    suspend fun markDownloaded(id: String, path: String)

    @Query("UPDATE tracks SET isDownloaded = 0, downloadPath = NULL WHERE id = :id")
    suspend fun markNotDownloaded(id: String)

    @Query("UPDATE tracks SET lastPlayedAt = :time, playCount = playCount + 1 WHERE id = :id")
    suspend fun markPlayed(id: String, time: Long = System.currentTimeMillis())

    @Query("SELECT COUNT(*) FROM tracks WHERE isDownloaded = 1")
    fun getDownloadedCount(): Flow<Int>

    @Query("SELECT * FROM tracks WHERE id IN (:ids)")
    suspend fun getByIds(ids: List<String>): List<Track>

    @Query("SELECT SUM(playCount) FROM tracks")
    fun getTotalPlayCount(): Flow<Int?>

    @Query("SELECT SUM(playCount * durationMs) FROM tracks")
    fun getTotalListeningTimeMs(): Flow<Long?>

    @Query("SELECT COUNT(DISTINCT artist) FROM tracks WHERE playCount > 0")
    fun getUniqueArtistCount(): Flow<Int>

    @Query("SELECT artist, SUM(playCount) as playCount FROM tracks WHERE playCount > 0 GROUP BY artist ORDER BY playCount DESC LIMIT :limit")
    fun getTopArtists(limit: Int = 10): Flow<List<ArtistPlayCount>>

    @Query("SELECT COUNT(*) FROM tracks")
    fun getTotalTrackCount(): Flow<Int>
}

data class ArtistPlayCount(
    val artist: String,
    val playCount: Int
)
