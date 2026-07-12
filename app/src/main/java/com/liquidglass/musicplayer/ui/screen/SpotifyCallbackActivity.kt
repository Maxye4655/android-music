package com.liquidglass.musicplayer.ui.screen

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import com.liquidglass.musicplayer.data.remote.SpotifyAuthApi
import com.liquidglass.musicplayer.data.remote.SpotifyAuthManager
import com.liquidglass.musicplayer.ui.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@AndroidEntryPoint
class SpotifyCallbackActivity : ComponentActivity() {

    @Inject lateinit var authManager: SpotifyAuthManager
    @Inject lateinit var authApi: SpotifyAuthApi

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        handleIntent(intent)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent) {
        val uri = intent.data
        if (uri != null && uri.toString().startsWith(authManager.redirectUri)) {
            val code = uri.getQueryParameter("code")
            val error = uri.getQueryParameter("error")

            if (error != null) {
                Toast.makeText(this, "Spotify authorization denied: $error", Toast.LENGTH_LONG).show()
                navigateToMain()
                return
            }

            if (code != null) {
                exchangeCode(code)
                return
            }
        }
        navigateToMain()
    }

    private fun exchangeCode(code: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = authApi.exchangeCode(
                    code = code,
                    redirectUri = authManager.redirectUri,
                    clientId = authManager.clientId,
                    clientSecret = ""
                )
                if (response.isSuccessful) {
                    response.body()?.let { token ->
                        authManager.saveTokens(
                            accessToken = token.accessToken,
                            refreshToken = token.refreshToken,
                            expiresIn = token.expiresIn
                        )
                    }
                }
            } catch (_: Exception) {
            }
            withContext(Dispatchers.Main) {
                navigateToMain()
            }
        }
    }

    private fun navigateToMain() {
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        }
        startActivity(intent)
        finish()
    }
}
