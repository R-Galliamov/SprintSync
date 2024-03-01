package com.developers.sprintsync.tracking.service.provider.time.stopwatch

import kotlinx.coroutines.flow.MutableStateFlow

interface Stopwatch {
    val timeMillisState: MutableStateFlow<Long>

    fun start()

    fun pause()

    fun reset()
}
