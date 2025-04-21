package com.developers.sprintsync.data.track.service.processing.segment

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

class SegmentGeneratingStateManager
    @Inject
    constructor() {
        private var _state = MutableStateFlow<SegmentGeneratingState>(SegmentGeneratingState.Uninitialized)
        val state get() = _state.asStateFlow()

        fun initializeState(data: TimedLocation) {
            _state.update { SegmentGeneratingState.Initialized(data) }
        }

        fun updateState(data: TimedLocation) {
            _state.update { SegmentGeneratingState.GeneratingSegments(data) }
        }

        fun reset() {
            _state.update { SegmentGeneratingState.Uninitialized }
        }
    }
