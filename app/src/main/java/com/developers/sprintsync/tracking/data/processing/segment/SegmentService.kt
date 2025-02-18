package com.developers.sprintsync.tracking.data.processing.segment

import android.util.Log
import com.developers.sprintsync.core.components.track.data.model.Segment
import com.developers.sprintsync.core.util.validation.ValidationException
import com.developers.sprintsync.tracking.data.model.TimedLocation
import com.developers.sprintsync.tracking.data.processing.util.validator.SegmentValidationError
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.update
import javax.inject.Inject

class SegmentService
    @Inject
    constructor(
        private val stateManager: SegmentGeneratingStateManager,
        private val generator: SegmentGenerator,
    ) {
        private val _data = MutableStateFlow<Segment?>(null)
        val data = _data.asStateFlow().filterNotNull()

        fun addTimedLocation(data: TimedLocation) {
            when (val currentState = stateManager.state.value) {
                SegmentGeneratingState.Uninitialized -> stateManager.initializeState(data)
                is SegmentGeneratingState.Initialized -> {
                    val startData = currentState.lastData
                    val segmentId = getNextSegmentId(_data.value)
                    generator.buildSegment(
                        segmentId = segmentId,
                        startData = startData,
                        endData = data,
                        onSuccess = { segment ->
                            Log.d(TAG, "New Segment: $segment")
                            _data.update { segment }
                            stateManager.updateState(data)
                        },
                        onFailure = { e ->
                            Log.e(TAG, "Error to generate segment: ${e.message}")
                            handleValidationErrors(e, data)
                        },
                    )
                }
            }
        }

        fun resetData() {
            _data.update { null }
            stateManager.reset()
        }

        private fun getNextSegmentId(currentSegment: Segment?): Long = (currentSegment?.id?.plus(1)) ?: 0L

        private fun handleValidationErrors(
            e: Throwable,
            data: TimedLocation,
        ) {
            if (e is ValidationException) {
                if (e.errors.contains(SegmentValidationError.PaceTooFast)) {
                    stateManager.updateState(data)
                }
            }
        }

        companion object {
            const val TAG = "My stack: SegmentService"
        }
    }
