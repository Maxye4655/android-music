package com.liquidglass.musicplayer.ui.screen.library

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.liquidglass.musicplayer.data.model.Album
import com.liquidglass.musicplayer.data.model.Playlist
import com.liquidglass.musicplayer.data.model.Track
import com.liquidglass.musicplayer.data.remote.SpotifyAuthManager
import com.liquidglass.musicplayer.domain.repository.LocalRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class LibraryUiState(
    val isSpotifyConnected: Boolean = false,
    val playlists: List<Playlist> = emptyList(),
    val albums: List<Album> = emptyList(),
    val recentlyPlayed: List<Track> = emptyList(),
    val downloadedTracks: List<Track> = emptyList(),
    val downloadedCount: Int = 0,
    val selectedTab: LibraryTab = LibraryTab.Playlists,
    val isLoading: Boolean = false
)

enum class LibraryTab(val label: String) {
    Playlists("Playlists"),
    Albums("Albums"),
    Artists("Artists"),
    Downloaded("Downloaded")
}

@HiltViewModel
class LibraryViewModel @Inject constructor(
    private val localRepository: LocalRepository,
    private val authManager: SpotifyAuthManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(LibraryUiState())
    val uiState: StateFlow<LibraryUiState> = _uiState.asStateFlow()

    init {
        observeAuthState()
        loadData()
    }

    private fun observeAuthState() {
        viewModelScope.launch {
            authManager.isAuthenticated.collect { isAuth ->
                _uiState.update { it.copy(isSpotifyConnected = isAuth) }
            }
        }
    }

    private fun loadData() {
        viewModelScope.launch {
            launch {
                localRepository.getAllPlaylists().collect { playlists ->
                    _uiState.update { it.copy(playlists = playlists) }
                }
            }

            launch {
                localRepository.getAllAlbums().collect { albums ->
                    _uiState.update { it.copy(albums = albums) }
                }
            }

            launch {
                localRepository.getRecentlyPlayed().collect { tracks ->
                    _uiState.update { it.copy(recentlyPlayed = tracks) }
                }
            }

            launch {
                localRepository.getDownloadedTracks().collect { tracks ->
                    _uiState.update {
                        it.copy(
                            downloadedTracks = tracks,
                            downloadedCount = tracks.size
                        )
                    }
                }
            }
        }
    }

    fun selectTab(tab: LibraryTab) {
        _uiState.update { it.copy(selectedTab = tab) }
    }

    fun disconnectSpotify() {
        authManager.clearTokens()
        _uiState.update { it.copy(isSpotifyConnected = false) }
    }
}
