package com.abhiram.intervaltimer

import android.app.Application
import android.os.SystemClock
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.ceil

enum class Phase(val label: String, val seconds: Int) {
    READY("GET READY", 3),
    WORK("WORK", 60),
    REST("REST", 120),
}

class TimerViewModel(app: Application) : AndroidViewModel(app) {

    var phase by mutableStateOf(Phase.READY)
        private set

    var secondsLeft by mutableIntStateOf(Phase.READY.seconds)
        private set

    var running by mutableStateOf(true)
        private set

    private val haptics = Haptics(app)

    init {
        viewModelScope.launch {
            runPhase(Phase.READY)
            var next = Phase.WORK
            while (true) {
                runPhase(next)
                next = if (next == Phase.WORK) Phase.REST else Phase.WORK
            }
        }
    }

    fun togglePause() {
        running = !running
    }

    // Subtracts measured elapsed time rather than counting ticks, so delay() jitter never accumulates.
    // While paused the clock still advances but the remainder does not, so resuming never jumps.
    private suspend fun runPhase(p: Phase) {
        phase = p
        secondsLeft = p.seconds
        haptics.cue(p)
        var msLeft = p.seconds * 1000L
        var last = SystemClock.elapsedRealtime()
        while (msLeft > 0) {
            delay(TICK_MS)
            val now = SystemClock.elapsedRealtime()
            val elapsed = now - last
            last = now
            if (!running) continue
            msLeft -= elapsed
            secondsLeft = ceil(msLeft / 1000.0).toInt().coerceAtLeast(0)
        }
    }

    private companion object {
        const val TICK_MS = 50L
    }
}
