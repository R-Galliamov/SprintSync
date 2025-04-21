package com.developers.sprintsync.data.track.service.processing.segment

import android.util.Log
import com.developers.sprintsync.data.track.service.processing.session.TimedLocation
import com.developers.sprintsync.domain.track.model.Segment
import com.developers.sprintsync.domain.track.use_case.service.SegmentGenerator
import com.developers.sprintsync.domain.track.use_case.validator.SegmentValidationException
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
                    generator.generateSegment(
                        segmentId = segmentId,
                        startData = startData,
                        endData = data,
                        onSuccess = { segment ->
                            Log.d(TAG, "New Segment: $segment")
                            _data.update { segment }
                            stateManager.updateState(data)
                        },
                        onFailure = { e ->
                            handleException(e, data)
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

        private fun handleException(
            e: Throwable,
            data: TimedLocation,
        ) {
            Log.e(TAG, "Error to generate segment", e)
            if (e is SegmentValidationException.PaceTooFast) {
                stateManager.updateState(data)
            }
        }

        companion object {
            const val TAG = "My stack: SegmentService"
        }
    }
