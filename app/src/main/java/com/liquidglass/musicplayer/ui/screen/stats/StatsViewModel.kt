package com.liquidglass.musicplayer.ui.screen.stats

import androidx.lifecycle.ViewModel
import com.liquidglass.musicplayer.data.local.TrackDao
import com.liquidglass.musicplayer.data.local.ArtistPlayCount
import com.liquidglass.musicplayer.data.model.Track
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

data class StatsUiState(
    val totalPlayCount: Int = 0,
    val totalListeningTimeMs: Long = 0L,
    val uniqueArtistCount: Int = 0,
    val totalTrackCount: Int = 0,
    val topTracks: List<Track> = emptyList(),
    val topArtists: List<ArtistPlayCount> = emptyList(),
    val isLoading: Boolean = true
) {
    val totalHours: Float get() = totalListeningTimeMs / (1000f * 60f * 60f)
    val totalMinutes: Int get() = (totalListeningTimeMs / (1000L * 60L)).toInt()
}

@HiltViewModel
class StatsViewModel @Inject constructor(
    private val trackDao: TrackDao
) : ViewModel() {

    val uiState: StateFlow<StatsUiState> = combine(
        trackDao.getTotalPlayCount(),
        trackDao.getTotalListeningTimeMs(),
        trackDao.getUniqueArtistCount(),
        trackDao.getTotalTrackCount(),
        trackDao.getTopTracks(10),
        trackDao.getTopArtists(10)
    ) { results ->
        StatsUiState(
            totalPlayCount = results[0] as? Int ?: 0,
            totalListeningTimeMs = results[1] as? Long ?: 0L,
            uniqueArtistCount = results[2] as? Int ?: 0,
            totalTrackCount = results[3] as? Int ?: 0,
            topTracks = results[4] as List<Track>,
            topArtists = results[5] as List<ArtistPlayCount>,
            isLoading = false
        )
    }.stateIn(
        scope = kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.Main),
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = StatsUiState()
    )
}
