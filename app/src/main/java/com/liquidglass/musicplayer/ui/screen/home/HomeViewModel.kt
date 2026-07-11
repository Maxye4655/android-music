package com.liquidglass.musicplayer.ui.screen.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.liquidglass.musicplayer.data.model.Album
import com.liquidglass.musicplayer.data.model.Playlist
import com.liquidglass.musicplayer.data.model.Track
import com.liquidglass.musicplayer.data.remote.SpotifyAuthManager
import com.liquidglass.musicplayer.domain.repository.LocalRepository
import com.liquidglass.musicplayer.domain.repository.SpotifyRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomeUiState(
    val isAuthenticated: Boolean = false,
    val recentlyPlayed: List<Track> = emptyList(),
    val topTracks: List<Track> = emptyList(),
    val playlists: List<Playlist> = emptyList(),
    val recommendations: List<Track> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val spotifyRepository: SpotifyRepository,
    private val localRepository: LocalRepository,
    private val authManager: SpotifyAuthManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        observeAuthState()
        loadData()
    }

    private fun observeAuthState() {
        viewModelScope.launch {
            authManager.isAuthenticated.collect { isAuth ->
                _uiState.update { it.copy(isAuthenticated = isAuth) }
                if (isAuth) loadData()
            }
        }
    }

    private fun loadData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            try {
                // Load local data
                launch {
                    localRepository.getRecentlyPlayed().collect { tracks ->
                        _uiState.update { it.copy(recentlyPlayed = tracks) }
                    }
                }

                launch {
                    localRepository.getTopTracks().collect { tracks ->
                        _uiState.update { it.copy(topTracks = tracks) }
                    }
                }

                // Load from Spotify
                if (authManager.isAuthenticated.value) {
                    launch {
                        spotifyRepository.getMyPlaylists().fold(
                            onSuccess = { playlists ->
                                _uiState.update { it.copy(playlists = playlists) }
                            },
                            onFailure = { e ->
                                _uiState.update { it.copy(error = e.message) }
                            }
                        )
                    }

                    launch {
                        spotifyRepository.getCategories().fold(
                            onSuccess = { categories ->
                                // Use categories to populate browse section
                            },
                            onFailure = { /* ignore */ }
                        )
                    }
                }
            } finally {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    fun loadRecommendations(trackIds: List<String>) {
        viewModelScope.launch {
            spotifyRepository.getRecommendations(trackIds).fold(
                onSuccess = { tracks ->
                    _uiState.update { it.copy(recommendations = tracks) }
                },
                onFailure = { e ->
                    _uiState.update { it.copy(error = e.message) }
                }
            )
        }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}
