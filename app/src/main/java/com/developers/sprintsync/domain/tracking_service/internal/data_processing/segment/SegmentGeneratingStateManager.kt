package com.developers.sprintsync.domain.tracking_service.internal.data_processing.segment

import com.developers.sprintsync.domain.tracking_service.model.TimedLocation
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

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
