package com.developers.sprintsync.tracking.data.provider.time.stopwatch

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class StopwatchImpl
    @Inject
    constructor() : Stopwatch {
        private var accumulatedTime: Long = INITIAL_TIME
        private var startTime: Long = INITIAL_TIME
        private var isRunning: Boolean = false
        private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())
        private var job: Job? = null

        private val _timeMillisState: MutableStateFlow<Long> = MutableStateFlow(INITIAL_TIME)
        override val timeMillisState get() = _timeMillisState.asStateFlow()

        override fun start() {
            if (isRunning) return
            startTime = System.currentTimeMillis()
            isRunning = true
            job =
                scope.launch {
                    while (true) {
                        _timeMillisState.value = getCurrentTime()
                        delay(UPDATE_INTERVAL)
                    }
                }
        }

        override fun pause() {
            if (!isRunning) return
            isRunning = false
            accumulatedTime += System.currentTimeMillis() - startTime
            startTime = INITIAL_TIME
            job?.cancel()
            job = null
            _timeMillisState.value = accumulatedTime
        }

        override fun reset() {
            isRunning = false
            job?.cancel()
            job = null
            accumulatedTime = INITIAL_TIME
            startTime = INITIAL_TIME
            _timeMillisState.value = INITIAL_TIME
        }

        private fun getCurrentTime(): Long =
            if (isRunning) {
                accumulatedTime + (System.currentTimeMillis() - startTime)
            } else {
                accumulatedTime
            }

        companion object {
            private const val INITIAL_TIME = 0L
            private const val UPDATE_INTERVAL = 1000L
        }
    }
