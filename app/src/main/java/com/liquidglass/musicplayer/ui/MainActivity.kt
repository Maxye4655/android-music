package com.liquidglass.musicplayer.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import com.liquidglass.musicplayer.ui.navigation.LiquidGlassNavHost
import com.liquidglass.musicplayer.ui.theme.LiquidGlassTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        setContent {
            LiquidGlassTheme {
                LiquidGlassNavHost(modifier = Modifier.fillMaxSize())
            }
        }
    }
}
