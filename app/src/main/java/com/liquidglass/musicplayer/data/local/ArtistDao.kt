package com.liquidglass.musicplayer.data.local

import androidx.room.*
import com.liquidglass.musicplayer.data.model.Artist
import kotlinx.coroutines.flow.Flow

@Dao
interface ArtistDao {

    @Query("SELECT * FROM artists WHERE isFollowed = 1 ORDER BY name ASC")
    fun getFollowed(): Flow<List<Artist>>

    @Query("SELECT * FROM artists WHERE id = :id")
    suspend fun getById(id: String): Artist?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(artist: Artist)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(artists: List<Artist>)

    @Update
    suspend fun update(artist: Artist)

    @Delete
    suspend fun delete(artist: Artist)
}
