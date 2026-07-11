package com.liquidglass.musicplayer.ui.screen

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.liquidglass.musicplayer.data.remote.SpotifyAuthManager
import com.liquidglass.musicplayer.ui.navigation.Screen
import com.liquidglass.musicplayer.ui.theme.LiquidGlassTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SpotifyCallbackActivity : ComponentActivity() {

    @Inject lateinit var authManager: SpotifyAuthManager

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
            if (code != null) {
                // Return the code to the calling activity
                val resultIntent = Intent().apply {
                    putExtra("auth_code", code)
                }
                setResult(Activity.RESULT_OK, resultIntent)
            } else {
                val error = uri.getQueryParameter("error")
                val resultIntent = Intent().apply {
                    putExtra("auth_error", error ?: "Unknown error")
                }
                setResult(Activity.RESULT_CANCELED, resultIntent)
            }
        }
        finish()
    }
}
