package com.developers.sprintsync.data.track.service.processing.segment

import com.developers.sprintsync.core.util.log.AppLogger
import com.developers.sprintsync.data.track.service.processing.session.TimedLocation
import com.developers.sprintsync.domain.track.model.Segment
import com.developers.sprintsync.domain.track.validator.SegmentValidationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.update
import javax.inject.Inject

// Manages segment generation and state for location tracking
class SegmentService
@Inject
constructor(
    private val stateManager: SegmentGeneratingStateManager,
    private val segmentBuilder: SegmentBuilder,
    private val log: AppLogger,
) {
    private val _data = MutableStateFlow<Segment?>(null)
    val data = _data.asStateFlow().filterNotNull()

    private val timedLocations: MutableList<TimedLocation> = mutableListOf()

    // Processes new location data to generate segments
    fun addTimedLocation(data: TimedLocation) {
        when (val currentState = stateManager.state.value) {
            is SegmentGeneratingState.Uninitialized -> stateManager.initializeState(data)
            is SegmentGeneratingState.Initialized -> {
                val startData = currentState.lastData
                val segmentId = getNextSegmentId(_data.value)
                segmentBuilder.build(
                    id = segmentId,
                    startData = startData,
                    endData = data,
                ).fold(
                    onSuccess = { segment ->
                        log.i("New Segment generated: $segment")
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

    // Resets segment data and state
    fun resetData() {
        _data.update { null }
        stateManager.reset()
        log.i("Data and state reset")
    }

    private fun getNextSegmentId(currentSegment: Segment?): Long = (currentSegment?.id?.plus(1)) ?: 0L

    // Handles errors during segment generation
    private fun handleException(
        e: Throwable,
        data: TimedLocation,
    ) {
        log.e("Error generating segment: ${e.message}", e)
        if (e is SegmentValidationException.PaceTooFast) {
            stateManager.updateState(data)
            log.w("Pace too fast, updated state with data: $data")
        }
    }
}
