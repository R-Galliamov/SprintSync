package com.developers.sprintsync.data.track.service.processing.segment

import com.developers.sprintsync.core.util.log.AppLogger
import com.developers.sprintsync.data.track.service.processing.session.TrackPoint
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

sealed class SegmentGeneratingState {
    data object Uninitialized : SegmentGeneratingState()

    open class Initialized(
        val lastPoint: TrackPoint,
    ) : SegmentGeneratingState()

    class GeneratingSegments(
        lastPoint: TrackPoint,
    ) : Initialized(lastPoint)
}

// Manages and stores segments generating state for a session
class SegmentGeneratingStateManager
    @Inject
    constructor(
        private val log: AppLogger
    ) {
        private var _state = MutableStateFlow<SegmentGeneratingState>(SegmentGeneratingState.Uninitialized)
        val state get() = _state.asStateFlow()

        fun initializeState(point: TrackPoint) {
            _state.update { SegmentGeneratingState.Initialized(point) }
            log.i("State initialized with data: $point")
        }

        fun updateState(point: TrackPoint) {
            _state.update { SegmentGeneratingState.GeneratingSegments(point) }
            log.i("State updated to GeneratingSegments with data: $point")
        }

        fun reset() {
            _state.update { SegmentGeneratingState.Uninitialized }
            log.i("State reset to Uninitialized")
        }
    }
