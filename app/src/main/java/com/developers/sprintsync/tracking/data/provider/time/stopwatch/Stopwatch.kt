package com.developers.sprintsync.tracking.data.provider.time.stopwatch

import kotlinx.coroutines.flow.StateFlow

interface Stopwatch {
    val timeMillisState: StateFlow<Long>

    fun start()

    fun pause()

    fun reset()
}
