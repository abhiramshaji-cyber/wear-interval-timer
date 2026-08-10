package com.abhiram.intervaltimer

import android.content.Context

const val STEP_SECONDS = 5
val DURATION_RANGE = 5..600

fun snapDuration(seconds: Int) = (seconds / STEP_SECONDS * STEP_SECONDS).coerceIn(DURATION_RANGE)

class IntervalStore(context: Context) {

    private val prefs = context.getSharedPreferences("intervals", Context.MODE_PRIVATE)

    val work: Int get() = snapDuration(prefs.getInt(KEY_WORK, DEFAULT_WORK))
    val rest: Int get() = snapDuration(prefs.getInt(KEY_REST, DEFAULT_REST))

    fun save(work: Int, rest: Int) {
        prefs.edit()
            .putInt(KEY_WORK, snapDuration(work))
            .putInt(KEY_REST, snapDuration(rest))
            .apply()
    }

    private companion object {
        const val KEY_WORK = "work_seconds"
        const val KEY_REST = "rest_seconds"
        const val DEFAULT_WORK = 60
        const val DEFAULT_REST = 120
    }
}
