package com.liquidglass.musicplayer.util

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager

object HapticFeedback {

    fun lightTap(context: Context) {
        vibrate(context, VibrationEffect.createPredefined(VibrationEffect.EFFECT_TICK))
    }

    fun mediumTap(context: Context) {
        vibrate(context, VibrationEffect.createPredefined(VibrationEffect.EFFECT_CLICK))
    }

    fun heavyTap(context: Context) {
        vibrate(context, VibrationEffect.createPredefined(VibrationEffect.EFFECT_HEAVY_CLICK))
    }

    @Suppress("DEPRECATION")
    private fun vibrate(context: Context, effect: VibrationEffect) {
        val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val manager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            manager.defaultVibrator
        } else {
            context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        }
        vibrator.vibrate(effect)
    }
}
