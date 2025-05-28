package com.developers.sprintsync.data.track.service.processing.segment

import com.developers.sprintsync.core.util.log.AppLogger
import com.developers.sprintsync.data.track.service.processing.session.TimedLocation
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

sealed class SegmentGeneratingState {
    data object Uninitialized : SegmentGeneratingState()

    open class Initialized(
        val lastData: TimedLocation,
    ) : SegmentGeneratingState()

    class GeneratingSegments(
        lastData: TimedLocation,
    ) : Initialized(lastData)
}

// Manages and stores segments generating state for a session
class SegmentGeneratingStateManager
    @Inject
    constructor(
        private val log: AppLogger
    ) {
        private var _state = MutableStateFlow<SegmentGeneratingState>(SegmentGeneratingState.Uninitialized)
        val state get() = _state.asStateFlow()

        fun initializeState(data: TimedLocation) {
            _state.update { SegmentGeneratingState.Initialized(data) }
            log.i("State initialized with data: $data")
        }

        fun updateState(data: TimedLocation) {
            _state.update { SegmentGeneratingState.GeneratingSegments(data) }
            log.i("State updated to GeneratingSegments with data: $data")
        }

        fun reset() {
            _state.update { SegmentGeneratingState.Uninitialized }
            log.i("State reset to Uninitialized")
        }
    }
