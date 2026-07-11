package com.liquidglass.musicplayer.domain.repository

import android.content.Context
import com.liquidglass.musicplayer.data.local.AlbumDao
import com.liquidglass.musicplayer.data.local.PlaylistDao
import com.liquidglass.musicplayer.data.local.TrackDao
import com.liquidglass.musicplayer.data.model.*
import com.liquidglass.musicplayer.data.remote.*
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SpotifyRepository @Inject constructor(
    private val spotifyApi: SpotifyApi,
    private val trackDao: TrackDao,
    private val playlistDao: PlaylistDao,
    private val albumDao: AlbumDao,
    private val authManager: SpotifyAuthManager
) {

    suspend fun search(query: String): Result<SpotifySearchResponse> = runCatching {
        spotifyApi.search(query).body() ?: throw Exception("Empty response")
    }

    fun searchTracks(query: String): Flow<List<Track>> = flow {
        val response = spotifyApi.search(query, type = "track")
        response.body()?.tracks?.items?.let { spotifyTracks ->
            val tracks = spotifyTracks.map { it.toTrack() }
            trackDao.insert(tracks)
            emit(tracks)
        } ?: emit(emptyList())
    }.flowOn(Dispatchers.IO)

    suspend fun getTrack(id: String): Result<Track> = runCatching {
        val response = spotifyApi.getTrack(id)
        val track = response.body()?.toTrack() ?: throw Exception("Track not found")
        trackDao.insert(track)
        track
    }

    suspend fun getTracks(ids: List<String>): Result<List<Track>> = runCatching {
        val response = spotifyApi.getTracks(ids.joinToString(","))
        response.body()?.values?.map { it.toTrack() } ?: emptyList()
    }

    suspend fun getAlbum(id: String): Result<Pair<Album, List<Track>>> = runCatching {
        val albumResponse = spotifyApi.getAlbum(id)
        val album = albumResponse.body()?.toAlbum() ?: throw Exception("Album not found")
        albumDao.insert(album)

        val tracksResponse = spotifyApi.getAlbumTracks(id)
        val tracks = tracksResponse.body()?.items?.map { it.toTrack() } ?: emptyList()
        trackDao.insert(tracks)

        album to tracks
    }

    suspend fun getArtistTopTracks(id: String): Result<List<Track>> = runCatching {
        val response = spotifyApi.getArtistTopTracks(id)
        val tracks = response.body()?.get("tracks")?.map { it.toTrack() } ?: emptyList()
        trackDao.insert(tracks)
        tracks
    }

    suspend fun getMyPlaylists(): Result<List<Playlist>> = runCatching {
        val response = spotifyApi.getMyPlaylists()
        val playlists = response.body()?.items?.map { it.toPlaylist() } ?: emptyList()
        playlistDao.insert(playlists)
        playlists
    }

    suspend fun getPlaylist(id: String): Result<Pair<Playlist, List<Track>>> = runCatching {
        val playlistResponse = spotifyApi.getPlaylist(id)
        val playlist = playlistResponse.body()?.toPlaylist() ?: throw Exception("Playlist not found")
        playlistDao.insert(playlist)

        val tracksResponse = spotifyApi.getPlaylistTracks(id)
        val tracks = tracksResponse.body()?.items
            ?.mapNotNull { it.track?.toTrack() }
            ?: emptyList()
        trackDao.insert(tracks)

        playlist to tracks
    }

    suspend fun getSavedTracks(): Result<List<Track>> = runCatching {
        val response = spotifyApi.getSavedTracks()
        val tracks = response.body()?.items
            ?.mapNotNull { it.track?.toTrack() }
            ?: emptyList()
        trackDao.insert(tracks)
        tracks
    }

    suspend fun getRecommendations(seedTrackIds: List<String>): Result<List<Track>> = runCatching {
        val response = spotifyApi.getRecommendations(
            seedTracks = seedTrackIds.joinToString(",")
        )
        val tracks = response.body()?.tracks?.map { it.toTrack() } ?: emptyList()
        trackDao.insert(tracks)
        tracks
    }

    suspend fun getCategories(): Result<List<SpotifyCategory>> = runCatching {
        val response = spotifyApi.getCategories()
        response.body()?.categories?.items ?: emptyList()
    }

    suspend fun getCategoryPlaylists(categoryId: String): Result<List<Playlist>> = runCatching {
        val response = spotifyApi.getCategoryPlaylists(categoryId)
        val playlists = response.body()?.get("playlists")?.items?.map { it.toPlaylist() } ?: emptyList()
        playlistDao.insert(playlists)
        playlists
    }

    suspend fun transferPlayback(deviceId: String): Result<Unit> = runCatching {
        spotifyApi.transferPlayback(mapOf("device_ids" to listOf(deviceId)))
    }

    // Mapping extensions
    private fun SpotifyTrack.toTrack(): Track {
        val albumImages = album.images
        val bestImage = albumImages.maxByOrNull { (it.width ?: 0) * (it.height ?: 0) }
        val thumbnail = albumImages.minByOrNull { (it.width ?: Int.MAX_VALUE) * (it.height ?: Int.MAX_VALUE) }

        return Track(
            id = id,
            name = name,
            artist = artists.firstOrNull()?.name ?: "Unknown",
            artistId = artists.firstOrNull()?.id ?: "",
            album = album.name,
            albumId = album.id,
            albumArtUrl = thumbnail?.url ?: "",
            albumArtUrlLarge = bestImage?.url ?: "",
            durationMs = duration_ms,
            previewUrl = preview_url,
            uri = uri,
            isExplicit = explicit,
            popularity = popularity
        )
    }

    private fun SpotifyAlbum.toAlbum(): Album {
        val bestImage = images.maxByOrNull { (it.width ?: 0) * (it.height ?: 0) }
        val thumbnail = images.minByOrNull { (it.width ?: Int.MAX_VALUE) * (it.height ?: Int.MAX_VALUE) }

        return Album(
            id = id,
            name = name,
            artist = artists.firstOrNull()?.name ?: "Unknown",
            artistId = artists.firstOrNull()?.id ?: "",
            coverUrl = thumbnail?.url ?: "",
            coverUrlLarge = bestImage?.url ?: "",
            releaseDate = release_date,
            totalTracks = total_tracks
        )
    }

    private fun SpotifyPlaylist.toPlaylist(): Playlist {
        val bestImage = images.maxByOrNull { (it.width ?: 0) * (it.height ?: 0) }
        val thumbnail = images.minByOrNull { (it.width ?: Int.MAX_VALUE) * (it.height ?: Int.MAX_VALUE) }

        return Playlist(
            id = id,
            name = name,
            description = description ?: "",
            ownerName = owner.display_name ?: owner.id,
            ownerUrl = owner.href,
            coverUrl = thumbnail?.url ?: "",
            coverUrlLarge = bestImage?.url ?: "",
            trackCount = tracks?.total ?: 0
        )
    }
}
