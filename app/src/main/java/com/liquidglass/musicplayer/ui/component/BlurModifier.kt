package com.liquidglass.musicplayer.ui.component

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Stable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.unit.Dp

@Stable
fun Modifier.blurBackground(cornerRadius: Dp): Modifier {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        this.blur(cornerRadius)
    } else {
        this
    }
}
