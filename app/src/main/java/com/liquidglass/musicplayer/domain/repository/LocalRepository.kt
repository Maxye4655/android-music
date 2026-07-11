package com.liquidglass.musicplayer.domain.repository

import com.liquidglass.musicplayer.data.local.AlbumDao
import com.liquidglass.musicplayer.data.local.PlaylistDao
import com.liquidglass.musicplayer.data.local.SearchHistoryDao
import com.liquidglass.musicplayer.data.local.TrackDao
import com.liquidglass.musicplayer.data.model.*
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocalRepository @Inject constructor(
    private val trackDao: TrackDao,
    private val playlistDao: PlaylistDao,
    private val albumDao: AlbumDao,
    private val searchHistoryDao: SearchHistoryDao
) {

    // Tracks
    fun getRecentlyPlayed(): Flow<List<Track>> = trackDao.getRecentlyPlayed()
    fun getDownloadedTracks(): Flow<List<Track>> = trackDao.getDownloadedTracks()
    fun getTopTracks(limit: Int = 20): Flow<List<Track>> = trackDao.getTopTracks(limit)
    fun searchLocalTracks(query: String): Flow<List<Track>> = trackDao.searchLocal(query)
    fun getDownloadedCount(): Flow<Int> = trackDao.getDownloadedCount()
    suspend fun getTrack(id: String): Track? = trackDao.getTrack(id)
    suspend fun insertTrack(track: Track) = trackDao.insert(track)
    suspend fun insertTracks(tracks: List<Track>) = trackDao.insert(tracks)
    suspend fun updateTrack(track: Track) = trackDao.update(track)
    suspend fun deleteTrack(track: Track) = trackDao.delete(track)
    suspend fun markDownloaded(id: String, path: String) = trackDao.markDownloaded(id, path)
    suspend fun markNotDownloaded(id: String) = trackDao.markNotDownloaded(id)
    suspend fun markPlayed(id: String) = trackDao.markPlayed(id)
    suspend fun getTracksByIds(ids: List<String>): List<Track> = trackDao.getByIds(ids)

    // Playlists
    fun getAllPlaylists(): Flow<List<Playlist>> = playlistDao.getAll()
    fun getOwnPlaylists(): Flow<List<Playlist>> = playlistDao.getOwnPlaylists()
    fun getPlaylistFlow(id: String): Flow<Playlist?> = playlistDao.getByIdFlow(id)
    suspend fun getPlaylist(id: String): Playlist? = playlistDao.getById(id)
    suspend fun insertPlaylist(playlist: Playlist) = playlistDao.insert(playlist)
    suspend fun insertPlaylists(playlists: List<Playlist>) = playlistDao.insert(playlists)
    suspend fun deletePlaylist(id: String) = playlistDao.deleteById(id)

    // Albums
    fun getAllAlbums(): Flow<List<Album>> = albumDao.getAll()
    fun getAlbumFlow(id: String): Flow<Album?> = albumDao.getByIdFlow(id)
    suspend fun insertAlbum(album: Album) = albumDao.insert(album)

    // Search History
    fun getRecentSearches(limit: Int = 10): Flow<List<SearchHistory>> =
        searchHistoryDao.getRecent(limit)

    suspend fun addSearch(query: String) = searchHistoryDao.insert(SearchHistory(query = query))
    suspend fun clearSearchHistory() = searchHistoryDao.clearAll()
    suspend fun deleteSearch(query: String) = searchHistoryDao.deleteByQuery(query)
}
