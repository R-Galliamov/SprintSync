package com.developers.sprintsync.data.track.service.provider

import com.developers.sprintsync.core.util.log.AppLogger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

// Interface for providing duration tracking functionality
interface DurationProvider {
    val isRunning: Boolean

    val durationMillisFlow: Flow<Long>

    fun start()

    fun pause()

    fun reset()
}

// Implementation of DurationProvider for tracking elapsed time
class DurationProviderImpl
@Inject
constructor(
    private val scope: CoroutineScope,
    private val log: AppLogger
) : DurationProvider {
    private var accumulatedTime: Long = INITIAL_TIME
    private var startTime: Long = INITIAL_TIME
    private var job: Job? = null

    private var _isRunning: Boolean = false
    override val isRunning get() = _isRunning

    private val _durationMillisFlow: MutableStateFlow<Long> = MutableStateFlow(INITIAL_TIME)
    override val durationMillisFlow get() = _durationMillisFlow.asStateFlow()

    // Starts tracking duration
    override fun start() {
        if (_isRunning) {
            log.i("DurationProvider already running, skipping start")
            return
        }

        job?.cancel()
        startTime = System.currentTimeMillis()
        _isRunning = true

        job = startDurationUpdateJob()
        log.i("DurationProvider started")
    }

    // Pauses duration tracking
    override fun pause() {
        if (!_isRunning) {
            log.i("DurationProvider not running, skipping pause")
            return
        }

        _isRunning = false
        accumulatedTime += System.currentTimeMillis() - startTime
        startTime = INITIAL_TIME
        job?.cancel()
        job = null
        _durationMillisFlow.value = accumulatedTime
        log.i("DurationProvider paused, accumulated time: $accumulatedTime ms")
    }

    // Resets duration tracking
    override fun reset() {
        _isRunning = false
        job?.cancel()
        job = null

        log.i("DurationProvider reset, accumulated time: $accumulatedTime ms")

        accumulatedTime = INITIAL_TIME
        startTime = INITIAL_TIME
        _durationMillisFlow.value = INITIAL_TIME
    }

    // Starts a coroutine job to update duration flow
    private fun startDurationUpdateJob() =  scope.launch {
        while (_isRunning) {
            val elapsed = System.currentTimeMillis() - startTime
            _durationMillisFlow.value = accumulatedTime + elapsed
            delay(UPDATE_INTERVAL)
        }
    }

    companion object {
        private const val INITIAL_TIME = 0L
        private const val UPDATE_INTERVAL = 1000L
    }
}
