package com.developers.sprintsync.core.tracking_service.provider.time.stopwatch

import kotlinx.coroutines.flow.MutableStateFlow

interface Stopwatch {
    val timeMillisState: MutableStateFlow<Long>

    fun start()

    fun pause()

    fun reset()
}
