package com.liquidglass.musicplayer.ui.screen.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.liquidglass.musicplayer.data.model.Album
import com.liquidglass.musicplayer.data.model.Playlist
import com.liquidglass.musicplayer.data.model.SearchHistory
import com.liquidglass.musicplayer.data.model.Track
import com.liquidglass.musicplayer.data.remote.SpotifyAuthManager
import com.liquidglass.musicplayer.domain.repository.LocalRepository
import com.liquidglass.musicplayer.domain.repository.SpotifyRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SearchUiState(
    val query: String = "",
    val tracks: List<Track> = emptyList(),
    val albums: List<Album> = emptyList(),
    val playlists: List<Playlist> = emptyList(),
    val searchHistory: List<SearchHistory> = emptyList(),
    val isLoading: Boolean = false,
    val showResults: Boolean = false
)

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val spotifyRepository: SpotifyRepository,
    private val localRepository: LocalRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()

    private var searchJob: Job? = null

    init {
        loadSearchHistory()
    }

    private fun loadSearchHistory() {
        viewModelScope.launch {
            localRepository.getRecentSearches().collect { history ->
                _uiState.update { it.copy(searchHistory = history) }
            }
        }
    }

    fun onQueryChange(query: String) {
        _uiState.update { it.copy(query = query) }

        searchJob?.cancel()
        if (query.isBlank()) {
            _uiState.update {
                it.copy(
                    tracks = emptyList(),
                    albums = emptyList(),
                    playlists = emptyList(),
                    showResults = false
                )
            }
            return
        }

        searchJob = viewModelScope.launch {
            delay(300) // debounce
            performSearch(query)
        }
    }

    private suspend fun performSearch(query: String) {
        _uiState.update { it.copy(isLoading = true) }

        spotifyRepository.search(query).fold(
            onSuccess = { response ->
                val tracks = response.tracks?.items?.map { spotifyTrack ->
                    Track(
                        id = spotifyTrack.id,
                        name = spotifyTrack.name,
                        artist = spotifyTrack.artists.firstOrNull()?.name ?: "Unknown",
                        artistId = spotifyTrack.artists.firstOrNull()?.id ?: "",
                        album = spotifyTrack.album.name,
                        albumId = spotifyTrack.album.id,
                        albumArtUrl = spotifyTrack.album.images.lastOrNull()?.url ?: "",
                        albumArtUrlLarge = spotifyTrack.album.images.maxByOrNull { (it.width ?: 0) * (it.height ?: 0) }?.url ?: "",
                        durationMs = spotifyTrack.duration_ms,
                        previewUrl = spotifyTrack.preview_url,
                        uri = spotifyTrack.uri,
                        isExplicit = spotifyTrack.explicit,
                        popularity = spotifyTrack.popularity
                    )
                } ?: emptyList()

                val albums = response.albums?.items?.map { spotifyAlbum ->
                    Album(
                        id = spotifyAlbum.id,
                        name = spotifyAlbum.name,
                        artist = spotifyAlbum.artists.firstOrNull()?.name ?: "Unknown",
                        artistId = spotifyAlbum.artists.firstOrNull()?.id ?: "",
                        coverUrl = spotifyAlbum.images.lastOrNull()?.url ?: "",
                        coverUrlLarge = spotifyAlbum.images.maxByOrNull { (it.width ?: 0) * (it.height ?: 0) }?.url ?: "",
                        releaseDate = spotifyAlbum.release_date,
                        totalTracks = spotifyAlbum.total_tracks
                    )
                } ?: emptyList()

                _uiState.update {
                    it.copy(
                        tracks = tracks,
                        albums = albums,
                        isLoading = false,
                        showResults = true
                    )
                }
            },
            onFailure = {
                _uiState.update { it.copy(isLoading = false) }
            }
        )
    }

    fun submitSearch() {
        val query = _uiState.value.query
        if (query.isNotBlank()) {
            viewModelScope.launch {
                localRepository.addSearch(query)
            }
        }
    }

    fun clearQuery() {
        _uiState.update {
            it.copy(
                query = "",
                tracks = emptyList(),
                albums = emptyList(),
                playlists = emptyList(),
                showResults = false
            )
        }
    }

    fun clearHistory() {
        viewModelScope.launch {
            localRepository.clearSearchHistory()
        }
    }

    fun deleteHistoryItem(query: String) {
        viewModelScope.launch {
            localRepository.deleteSearch(query)
        }
    }
}
