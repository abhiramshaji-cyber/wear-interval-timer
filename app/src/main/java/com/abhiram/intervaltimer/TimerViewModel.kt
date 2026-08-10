package com.abhiram.intervaltimer

import android.app.Application
import android.os.SystemClock
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.ceil

enum class Phase(val label: String) {
    READY("GET READY"),
    WORK("WORK"),
    REST("REST"),
}

class TimerViewModel(app: Application) : AndroidViewModel(app) {

    var phase by mutableStateOf(Phase.READY)
        private set

    var secondsLeft by mutableIntStateOf(READY_SECONDS)
        private set

    var running by mutableStateOf(true)
        private set

    private val store = IntervalStore(app)

    var workSeconds by mutableIntStateOf(store.work)
        private set

    var restSeconds by mutableIntStateOf(store.rest)
        private set

    private val haptics = Haptics(app)
    private var loop: Job? = null

    init {
        reset()
    }

    fun togglePause() {
        running = !running
    }

    fun pause() {
        running = false
    }

    fun applyDurations(work: Int, rest: Int) {
        store.save(work, rest)
        workSeconds = store.work
        restSeconds = store.rest
        reset()
    }

    /** Restarts the whole cycle from GET READY, un-pausing if paused. */
    fun reset() {
        val previous = loop
        loop = viewModelScope.launch {
            // Must fully die before the new loop starts, or its last tick stomps the reset state.
            previous?.cancelAndJoin()
            running = true
            runPhase(Phase.READY)
            var next = Phase.WORK
            while (true) {
                runPhase(next)
                next = if (next == Phase.WORK) Phase.REST else Phase.WORK
            }
        }
    }

    // Subtracts measured elapsed time rather than counting ticks, so delay() jitter never accumulates.
    // While paused the clock still advances but the remainder does not, so resuming never jumps.
    private suspend fun runPhase(p: Phase) {
        val seconds = when (p) {
            Phase.READY -> READY_SECONDS
            Phase.WORK -> workSeconds
            Phase.REST -> restSeconds
        }
        phase = p
        secondsLeft = seconds
        haptics.cue(p)
        var msLeft = seconds * 1000L
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
        const val READY_SECONDS = 3
    }
}
