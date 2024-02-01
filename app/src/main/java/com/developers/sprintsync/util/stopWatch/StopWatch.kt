package com.developers.sprintsync.util.stopWatch

import kotlinx.coroutines.flow.MutableStateFlow

interface StopWatch {

    val timeMillisState: MutableStateFlow<Long>

    fun start()

    fun pause()

    fun reset()
}