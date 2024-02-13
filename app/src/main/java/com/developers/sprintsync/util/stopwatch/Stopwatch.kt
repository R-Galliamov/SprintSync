package com.developers.sprintsync.util.stopwatch

import kotlinx.coroutines.flow.MutableStateFlow

interface Stopwatch {

    val timeMillisState: MutableStateFlow<Long>

    fun start()

    fun pause()

    fun reset()
}