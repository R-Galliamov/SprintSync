package com.developers.sprintsync.service.tracker

import com.developers.sprintsync.util.stopwatch.Stopwatch
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class TimeTracker @Inject constructor(
    private val stopwatch: Stopwatch
) {
    fun timeInMillisFlow(): Flow<Long> = stopwatch.timeMillisState

    fun updateStopwatchState(isActive: Boolean) {
        when (isActive) {
            true -> stopwatch.start()
            false -> stopwatch.pause()
        }
    }
}
