package com.abhiram.intervaltimer

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager

/** One distinguishable buzz per phase so you never have to look at the watch mid-set. */
class Haptics(context: Context) {

    private val vibrator: Vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        (context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager).defaultVibrator
    } else {
        @Suppress("DEPRECATION")
        context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    }

    fun cue(phase: Phase) {
        if (!vibrator.hasVibrator()) return
        val effect = when (phase) {
            Phase.READY -> VibrationEffect.createOneShot(80, VibrationEffect.DEFAULT_AMPLITUDE)
            Phase.WORK -> VibrationEffect.createWaveform(longArrayOf(0, 180, 120, 180), -1)
            Phase.REST -> VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE)
        }
        vibrator.vibrate(effect)
    }
}
