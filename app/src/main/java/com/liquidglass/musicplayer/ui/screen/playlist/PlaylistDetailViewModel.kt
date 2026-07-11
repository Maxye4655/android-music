package com.liquidglass.musicplayer.ui.screen.playlist

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.liquidglass.musicplayer.data.model.Playlist
import com.liquidglass.musicplayer.data.model.Track
import com.liquidglass.musicplayer.domain.repository.SpotifyRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class PlaylistDetailUiState(
    val playlist: Playlist? = null,
    val tracks: List<Track> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class PlaylistDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val spotifyRepository: SpotifyRepository
) : ViewModel() {

    private val playlistId: String = savedStateHandle["playlistId"] ?: ""

    private val _uiState = MutableStateFlow(PlaylistDetailUiState())
    val uiState: StateFlow<PlaylistDetailUiState> = _uiState.asStateFlow()

    init {
        loadPlaylist()
    }

    private fun loadPlaylist() {
        if (playlistId.isBlank()) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            spotifyRepository.getPlaylist(playlistId).fold(
                onSuccess = { (playlist, tracks) ->
                    _uiState.update {
                        it.copy(
                            playlist = playlist,
                            tracks = tracks,
                            isLoading = false
                        )
                    }
                },
                onFailure = { e ->
                    _uiState.update {
                        it.copy(
                            error = e.message,
                            isLoading = false
                        )
                    }
                }
            )
        }
    }

    fun playAll() {
        val tracks = _uiState.value.tracks
        // TODO: trigger playback via PlayerViewModel
    }

    fun shufflePlay() {
        val tracks = _uiState.value.tracks.shuffled()
        // TODO: trigger shuffle playback via PlayerViewModel
    }
}
