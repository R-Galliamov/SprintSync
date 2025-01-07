package com.developers.sprintsync.core.tracking_service.provider.time.stopwatch

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class StopwatchImpl
    @Inject
    constructor() : Stopwatch {
        private var timeMillis = INITIAL_TIME
        private var lastTimestamp = INITIAL_TIME

        private var scope: CoroutineScope? = null
        private val dispatcher = Dispatchers.Default

        private var isActive = false

        override val timeMillisState: MutableStateFlow<Long> = MutableStateFlow(timeMillis)

        override fun start() {
            if (isActive) return
            initScope()
            scope?.launch {
                updateLastTimestamp()
                updateState(true)
                while (isActive) {
                    calculateElapsedTime()
                    updateLastTimestamp()
                    updateTimeMillisState()
                    delay(UPDATING_TIME_INTERVAL)
                }
            }
        }

        override fun pause() {
            updateState(false)
        }

        override fun reset() {
            resetData()
            cancelCoroutine()
            updateState(false)
        }

        private fun initScope() {
            scope = CoroutineScope(dispatcher)
        }

        private fun updateTimeMillisState() {
            timeMillisState.value = timeMillis
        }

        private fun updateLastTimestamp() {
            lastTimestamp = System.currentTimeMillis()
        }

        private fun calculateElapsedTime() {
            timeMillis += System.currentTimeMillis() - lastTimestamp
        }

        private fun updateState(isActive: Boolean) {
            this@StopwatchImpl.isActive = isActive
        }

        private fun resetData() {
            timeMillis = INITIAL_TIME
            lastTimestamp = INITIAL_TIME
            updateTimeMillisState()
        }

        private fun cancelCoroutine() {
            scope?.cancel()
            scope = null
        }

        companion object {
            private const val UPDATING_TIME_INTERVAL = 1000L
            private const val INITIAL_TIME = 0L
        }
    }
