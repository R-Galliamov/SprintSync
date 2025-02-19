package com.developers.sprintsync.tracking.data.processing.track

import com.developers.sprintsync.core.components.track.data.model.Segment
import com.developers.sprintsync.core.components.track.data.model.Track
import com.developers.sprintsync.tracking.data.model.TimedLocation
import com.developers.sprintsync.tracking.data.processing.segment.SegmentService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

class TrackFlowGenerator
    @Inject
    constructor(
        private val initializer: TrackInitializer,
        private val segmentService: SegmentService,
        private val trackUpdater: TrackUpdater,
    ) {
        private var _trackFlow = MutableStateFlow(Track.INITIAL)
        val trackFlow = _trackFlow.asStateFlow()
        val track get() = trackFlow.value

        private val scope = CoroutineScope(Dispatchers.IO)

        init {
            observeSegmentFlow()
        }

        private fun observeSegmentFlow() = scope.launch { segmentService.data.collect { handleNewSegment(it) } }

        fun addTimedLocation(
            data: TimedLocation,
        ) = segmentService.addTimedLocation(data)

        fun resetLastTimedLocation() {
            segmentService.resetData()
        }

        fun resetTrackingData() {
            _trackFlow.update { Track.INITIAL }
            resetLastTimedLocation()
        }

        private fun handleNewSegment(segment: Segment) {
            if (isTrackInitialized().not()) {
                initializeTrack(segment)
            } else {
                addSegmentToTrack(segment)
            }
        }

        private fun initializeTrack(segment: Segment) {
            val track = initializer.initializeTrack(segment)
            updateTrackFlow(track)
        }

        private fun addSegmentToTrack(segment: Segment) {
            val updatedTrack: Track =
                when (segment) {
                    is Segment.Active -> {
                        trackUpdater.updateTrackWithSegment(track, segment)
                    }

                    is Segment.Stationary -> {
                        trackUpdater.updateTrackWithSegment(track, segment)
                    }
                }
            updateTrackFlow(updatedTrack)
        }

        private fun isTrackInitialized(): Boolean = track != Track.INITIAL

        private fun updateTrackFlow(track: Track) {
            _trackFlow.update { track }
        }
    }
