package com.liquidglass.musicplayer.data.remote

import android.content.Context
import android.content.SharedPreferences
import com.liquidglass.musicplayer.BuildConfig
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SpotifyAuthManager @Inject constructor(
    @ApplicationContext private val context: Context
) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("spotify_auth", Context.MODE_PRIVATE)

    private val _accessToken = MutableStateFlow(getStoredToken())
    val accessToken: StateFlow<String?> = _accessToken.asStateFlow()

    private val _isAuthenticated = MutableStateFlow(getStoredToken() != null)
    val isAuthenticated: StateFlow<Boolean> = _isAuthenticated.asStateFlow()

    val clientId: String get() = BuildConfig.SPOTIFY_CLIENT_ID
    val redirectUri: String get() = BuildConfig.SPOTIFY_REDIRECT_URI

    fun getAuthorizationUrl(): String {
        val scopes = listOf(
            "streaming",
            "user-read-playback-state",
            "user-modify-playback-state",
            "user-read-currently-playing",
            "playlist-read-private",
            "playlist-read-collaborative",
            "user-library-read",
            "user-library-modify",
            "user-read-recently-played",
            "user-top-read"
        ).joinToString(" ")

        return "https://accounts.spotify.com/authorize" +
                "?client_id=$clientId" +
                "&response_type=code" +
                "&redirect_uri=${redirectUri}" +
                "&scope=${scopes}" +
                "&show_dialog=true"
    }

    fun handleAuthCallback(code: String) {
        // Token exchange happens in the repository layer
        // This method stores the result
    }

    fun saveTokens(accessToken: String, refreshToken: String?, expiresIn: Long) {
        val expiryTime = System.currentTimeMillis() + (expiresIn * 1000)
        prefs.edit()
            .putString(KEY_ACCESS_TOKEN, accessToken)
            .apply {
                refreshToken?.let { putString(KEY_REFRESH_TOKEN, it) }
                putLong(KEY_EXPIRY_TIME, expiryTime)
            }
            .apply()

        _accessToken.value = accessToken
        _isAuthenticated.value = true
    }

    fun getStoredRefreshToken(): String? {
        return prefs.getString(KEY_REFRESH_TOKEN, null)
    }

    fun clearTokens() {
        prefs.edit().clear().apply()
        _accessToken.value = null
        _isAuthenticated.value = false
    }

    fun isTokenExpired(): Boolean {
        val expiryTime = prefs.getLong(KEY_EXPIRY_TIME, 0)
        return System.currentTimeMillis() >= expiryTime
    }

    private fun getStoredToken(): String? {
        return if (!isTokenExpired()) {
            prefs.getString(KEY_ACCESS_TOKEN, null)
        } else {
            null
        }
    }

    companion object {
        private const val KEY_ACCESS_TOKEN = "access_token"
        private const val KEY_REFRESH_TOKEN = "refresh_token"
        private const val KEY_EXPIRY_TIME = "expiry_time"
    }
}
