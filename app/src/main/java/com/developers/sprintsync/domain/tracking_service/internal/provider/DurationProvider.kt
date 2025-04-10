package com.developers.sprintsync.domain.tracking_service.internal.provider

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

interface DurationProvider {
    val isRunning: Boolean

    val durationMillisFlow: StateFlow<Long>

    fun start()

    fun pause()

    fun reset()
}

class DefaultDurationProvider
    @Inject
    constructor(
        private val scope: CoroutineScope, // TODO pass update interval here
    ) : DurationProvider {
        private var accumulatedTime: Long = INITIAL_TIME
        private var startTime: Long = INITIAL_TIME
        private var job: Job? = null

        private var _isRunning: Boolean = false
        override val isRunning get() = _isRunning

        private val _durationMillisFlow: MutableStateFlow<Long> = MutableStateFlow(INITIAL_TIME)
        override val durationMillisFlow get() = _durationMillisFlow.asStateFlow()

        override fun start() {
            if (_isRunning) return

            job?.cancel()
            startTime = System.currentTimeMillis()
            _isRunning = true
            job =
                scope.launch {
                    while (_isRunning) {
                        val elapsed = System.currentTimeMillis() - startTime
                        _durationMillisFlow.value = accumulatedTime + elapsed
                        delay(UPDATE_INTERVAL)
                    }
                }
        }

        override fun pause() {
            if (!_isRunning) return

            _isRunning = false
            accumulatedTime += System.currentTimeMillis() - startTime
            startTime = INITIAL_TIME
            job?.cancel()
            job = null
            _durationMillisFlow.value = accumulatedTime
        }

        override fun reset() {
            _isRunning = false
            job?.cancel()
            job = null
            accumulatedTime = INITIAL_TIME
            startTime = INITIAL_TIME
            _durationMillisFlow.value = INITIAL_TIME
        }

        companion object {
            private const val INITIAL_TIME = 0L
            private const val UPDATE_INTERVAL = 1000L
        }
    }
