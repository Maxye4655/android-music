package com.liquidglass.musicplayer.data.local

import androidx.room.*
import com.liquidglass.musicplayer.data.model.Album
import kotlinx.coroutines.flow.Flow

@Dao
interface AlbumDao {

    @Query("SELECT * FROM albums ORDER BY addedAt DESC")
    fun getAll(): Flow<List<Album>>

    @Query("SELECT * FROM albums WHERE id = :id")
    suspend fun getById(id: String): Album?

    @Query("SELECT * FROM albums WHERE id = :id")
    fun getByIdFlow(id: String): Flow<Album?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(album: Album)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(albums: List<Album>)

    @Delete
    suspend fun delete(album: Album)

    @Query("DELETE FROM albums WHERE id = :id")
    suspend fun deleteById(id: String)
}
