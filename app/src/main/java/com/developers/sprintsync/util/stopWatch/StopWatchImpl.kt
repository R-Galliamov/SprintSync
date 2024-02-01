package com.developers.sprintsync.util.stopWatch

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class StopWatchImpl @Inject constructor() : StopWatch {

    private var coroutineScope = CoroutineScope(Dispatchers.Default)
    private var isActive = false

    private var timeMillis = 0L
    private var lastTimestamp = 0L

    override val timeMillisState: MutableStateFlow<Long> = MutableStateFlow(timeMillis)

    override fun start() {
        if (isActive) return

        coroutineScope.launch {
            lastTimestamp = System.currentTimeMillis()
            this@StopWatchImpl.isActive = true
            while (isActive) {
                delay(10L)
                timeMillis += System.currentTimeMillis() - lastTimestamp
                lastTimestamp = System.currentTimeMillis()
                timeMillisState.value = timeMillis
            }
        }
    }

    override fun pause() {
        isActive = false
    }

    override fun reset() {
        coroutineScope.cancel()
        coroutineScope = CoroutineScope(Dispatchers.Main)
        timeMillis = 0L
        lastTimestamp = 0L
        isActive = false
    }

}