package com.liquidglass.musicplayer.ui.screen.player

import android.app.Application
import android.content.Intent
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.liquidglass.musicplayer.data.model.Track
import com.liquidglass.musicplayer.data.remote.SpotifyAuthManager
import com.liquidglass.musicplayer.domain.repository.LocalRepository
import com.liquidglass.musicplayer.domain.repository.SpotifyRepository
import com.liquidglass.musicplayer.service.MusicPlaybackService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlayerViewModel @Inject constructor(
    application: Application,
    private val spotifyRepository: SpotifyRepository,
    private val localRepository: LocalRepository,
    private val authManager: SpotifyAuthManager
) : AndroidViewModel(application) {

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

    private val _repeatMode = MutableStateFlow(0)
    val repeatMode: StateFlow<Int> = _repeatMode.asStateFlow()

    private val _isLiked = MutableStateFlow(false)
    val isLiked: StateFlow<Boolean> = _isLiked.asStateFlow()

    fun playTrack(track: Track) {
        _currentTrack.value = track
        _isPlaying.value = true
        _queue.value = listOf(track)
        _progress.value = 0L
        _duration.value = track.durationMs

        localRepository.markPlayed(track.id)

        getApplication<Application>().startForegroundService(
            Intent(getApplication(), MusicPlaybackService::class.java).apply {
                putExtra("track_id", track.id)
                putExtra("track_name", track.name)
                putExtra("track_artist", track.artist)
                putExtra("track_album", track.album)
                putExtra("track_art_url", track.albumArtUrlLarge)
                putExtra("track_preview_url", track.previewUrl)
            }
        )
    }

    fun playQueue(tracks: List<Track>, startIndex: Int = 0) {
        if (tracks.isEmpty()) return
        _queue.value = tracks
        playTrack(tracks[startIndex])
    }

    fun togglePlayPause() {
        _isPlaying.value = !_isPlaying.value
    }

    fun seekTo(positionMs: Long) {
        _progress.value = positionMs
    }

    fun skipNext() {
        val current = _currentTrack.value ?: return
        val currentQueue = _queue.value
        val currentIndex = currentQueue.indexOfFirst { it.id == current.id }
        if (currentIndex < currentQueue.lastIndex) {
            playTrack(currentQueue[currentIndex + 1])
        }
    }

    fun skipPrevious() {
        val current = _currentTrack.value ?: return
        val currentQueue = _queue.value
        val currentIndex = currentQueue.indexOfFirst { it.id == current.id }
        if (currentIndex > 0) {
            playTrack(currentQueue[currentIndex - 1])
        }
    }

    fun toggleShuffle() {
        _shuffleMode.value = !_shuffleMode.value
    }

    fun toggleRepeat() {
        _repeatMode.value = when (_repeatMode.value) {
            0 -> 1
            1 -> 2
            else -> 0
        }
    }

    fun toggleLike() {
        _isLiked.value = !_isLiked.value
    }
}
