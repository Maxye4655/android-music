package com.liquidglass.musicplayer.service

import android.app.PendingIntent
import android.content.Intent
import android.os.Bundle
import androidx.annotation.OptIn
import androidx.media3.common.*
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import com.liquidglass.musicplayer.data.model.Track
import com.liquidglass.musicplayer.data.remote.OfflineManager
import com.liquidglass.musicplayer.data.remote.SpotifyApi
import com.liquidglass.musicplayer.ui.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@AndroidEntryPoint
class MusicPlaybackService : MediaSessionService() {

    @Inject lateinit var spotifyApi: SpotifyApi
    @Inject lateinit var offlineManager: OfflineManager

    private var mediaSession: MediaSession? = null
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    private val _currentTrack = MutableStateFlow<Track?>(null)
    val currentTrack: StateFlow<Track?> = _currentTrack.asStateFlow()

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    private val _progress = MutableStateFlow(0L)
    val progress: StateFlow<Long> = _progress.asStateFlow()

    private val _duration = MutableStateFlow(0L)
    val duration: StateFlow<Long> = _duration.asStateFlow()

    private val _queue = MutableStateFlow<List<Track>>(emptyList())
    val queue: StateFlow<List<Track>> = _queue.asStateFlow()

    private val _shuffleMode = MutableStateFlow(false)
    val shuffleMode: StateFlow<Boolean> = _shuffleMode.asStateFlow()

    private val _repeatMode = MutableStateFlow(RepeatMode.OFF)
    val repeatMode: StateFlow<Int> = _repeatMode.asStateFlow()

    private var progressJob: Job? = null

    @OptIn(UnstableApi::class)
    override fun onCreate() {
        super.onCreate()

        val player = ExoPlayer.Builder(this)
            .setHandleAudioBecomingNoisy(true)
            .setWakeMode(C.WAKE_MODE_NETWORK)
            .build()
            .apply {
                addListener(playerListener)
            }

        val intent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        mediaSession = MediaSession.Builder(this, player)
            .setSessionActivity(pendingIntent)
            .build()
    }

    @OptIn(UnstableApi::class)
    private val playerListener = object : Player.Listener {

        override fun onIsPlayingChanged(isPlaying: Boolean) {
            _isPlaying.value = isPlaying
            if (isPlaying) startProgressTracking()
            else stopProgressTracking()
        }

        override fun onPlaybackStateChanged(playbackState: Int) {
            when (playbackState) {
                Player.STATE_READY -> {
                    _duration.value = mediaSession?.player?.duration?.takeIf { it > 0 } ?: 0L
                }
                Player.STATE_ENDED -> {
                    onTrackEnded()
                }
            }
        }

        override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
            mediaItem?.localConfiguration?.tag?.let { trackId ->
                serviceScope.launch {
                    val track = _queue.value.find { it.id == trackId }
                    _currentTrack.value = track
                }
            }
        }
    }

    fun playTrack(track: Track, queue: List<Track> = listOf(track), startIndex: Int = 0) {
        _queue.value = queue
        _currentTrack.value = track

        val player = mediaSession?.player ?: return

        val mediaItems = queue.map { t ->
            MediaItem.Builder()
                .setUri(t.previewUrl ?: "")
                .setMediaId(t.id)
                .setMediaMetadata(
                    MediaMetadata.Builder()
                        .setTitle(t.name)
                        .setArtist(t.artist)
                        .setAlbumTitle(t.album)
                        .setArtworkUri(android.net.Uri.parse(t.albumArtUrlLarge))
                        .build()
                )
                .build()
        }

        player.setMediaItems(mediaItems, startIndex, 0)
        player.prepare()
        player.play()
    }

    fun playPreview(track: Track) {
        val player = mediaSession?.player ?: return
        _currentTrack.value = track

        val item = MediaItem.Builder()
            .setUri(track.previewUrl ?: "")
            .setMediaId(track.id)
            .setMediaMetadata(
                MediaMetadata.Builder()
                    .setTitle(track.name)
                    .setArtist(track.artist)
                    .setAlbumTitle(track.album)
                    .setArtworkUri(android.net.Uri.parse(track.albumArtUrlLarge))
                    .build()
            )
            .build()

        player.setMediaItem(item)
        player.prepare()
        player.play()
    }

    fun togglePlayPause() {
        val player = mediaSession?.player ?: return
        if (player.isPlaying) player.pause() else player.play()
    }

    fun seekTo(positionMs: Long) {
        mediaSession?.player?.seekTo(positionMs)
    }

    fun skipNext() {
        val player = mediaSession?.player ?: return
        if (player.hasNextMediaItem()) {
            player.seekToNext()
        }
    }

    fun skipPrevious() {
        val player = mediaSession?.player ?: return
        if (player.currentPosition > 3000) {
            player.seekTo(0)
        } else {
            player.seekToPrevious()
        }
    }

    fun toggleShuffle() {
        val player = mediaSession?.player ?: return
        val newMode = !_shuffleMode.value
        _shuffleMode.value = newMode
        player.shuffleModeEnabled = newMode
    }

    fun toggleRepeat() {
        val player = mediaSession?.player ?: return
        val newMode = when (_repeatMode.value) {
            RepeatMode.OFF -> RepeatMode.ALL
            RepeatMode.ALL -> RepeatMode.ONE
            else -> RepeatMode.OFF
        }
        _repeatMode.value = newMode
        player.repeatMode = newMode
    }

    private fun onTrackEnded() {
        if (_repeatMode.value == RepeatMode.ONE) {
            mediaSession?.player?.seekTo(0)
            mediaSession?.player?.play()
        } else {
            skipNext()
        }
    }

    private fun startProgressTracking() {
        stopProgressTracking()
        progressJob = serviceScope.launch {
            while (isActive) {
                mediaSession?.player?.let { player ->
                    _progress.value = player.currentPosition
                }
                delay(100)
            }
        }
    }

    private fun stopProgressTracking() {
        progressJob?.cancel()
        progressJob = null
    }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaSession? {
        return mediaSession
    }

    override fun onDestroy() {
        stopProgressTracking()
        mediaSession?.run {
            player.release()
            release()
        }
        mediaSession = null
        serviceScope.cancel()
        super.onDestroy()
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        val player = mediaSession?.player
        if (player == null || !player.playWhenReady || player.mediaItemCount == 0) {
            stopSelf()
        }
    }

    object RepeatMode {
        const val OFF = 0
        const val ALL = 1
        const val ONE = 2
    }
}
