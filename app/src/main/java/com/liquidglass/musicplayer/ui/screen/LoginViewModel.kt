package com.liquidglass.musicplayer.ui.screen

import androidx.lifecycle.ViewModel
import com.liquidglass.musicplayer.data.remote.SpotifyAuthManager
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    val authManager: SpotifyAuthManager
) : ViewModel()
