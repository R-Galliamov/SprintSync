package com.developers.sprintsync.tracking.session.service.monitor

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject

class Debouncer
    @Inject
    constructor() {
        private val scope = CoroutineScope(Dispatchers.Default)
        private var job: Job? = null

        fun debounce(
            delayMillis: Long = DEFAULT_DELAY_MILLIS,
            onDebounce: () -> Unit,
        ) {
            if (job?.isActive == true) {
                job?.cancel()
            }

            job =
                scope.launch {
                    if (isActive) {
                        delay(delayMillis)
                        onDebounce()
                    }
                }
        }

        fun cancel() {
            if (job?.isActive == true) {
                job?.cancel()
            }
        }

        companion object {
            private const val DEFAULT_DELAY_MILLIS = 10000L
        }
    }
