package com.developers.sprintsync.data.track.service.processing.segment

import com.developers.sprintsync.core.util.log.AppLogger
import com.developers.sprintsync.data.track.service.processing.calculator.pace.RunPaceAnalyzer
import com.developers.sprintsync.data.track.service.processing.session.TrackPoint
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
    private val paceAnalyzer: RunPaceAnalyzer,
    private val log: AppLogger,
) {
    private val _data = MutableStateFlow<Segment?>(null)
    val data = _data.asStateFlow().filterNotNull()

    private val trackPoints: MutableList<TrackPoint> = mutableListOf()

    // Processes new location data to generate segments
    fun addTimedLocation(point: TrackPoint) {
        when (val currentState = stateManager.state.value) {
            is SegmentGeneratingState.Uninitialized -> stateManager.initializeState(point)
            is SegmentGeneratingState.Initialized -> {
                val startPoint = currentState.lastPoint
                val segmentId = getNextSegmentId(_data.value)
                segmentBuilder.build(
                    id = segmentId,
                    startPoint = startPoint,
                    endPoint = point,
                ).fold(
                    onSuccess = { segment ->
                        log.i("New Segment generated: $segment")
                        _data.update { segment }
                        stateManager.updateState(point)
                    },
                    onFailure = { e ->
                        handleException(e, point)
                    },
                )
            }
        }
    }

    // Resets segment data and state
    fun resetData() {
        _data.update { null }
        paceAnalyzer.reset()
        stateManager.reset()
        log.i("Data and state reset")
    }

    private fun getNextSegmentId(currentSegment: Segment?): Long = (currentSegment?.id?.plus(1)) ?: 0L

    // Handles errors during segment generation
    private fun handleException(
        e: Throwable,
        data: TrackPoint,
    ) {
        log.e("Error generating segment: ${e.message}", e)
        if (e is SegmentValidationException.PaceTooFast) {
            stateManager.updateState(data)
            log.w("Pace too fast, updated state with data: $data")
        }
    }
}
