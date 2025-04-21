package com.developers.sprintsync.data.track.service.processing.track

import com.developers.sprintsync.data.track.service.processing.segment.SegmentService
import com.developers.sprintsync.data.track.service.processing.session.TimedLocation
import com.developers.sprintsync.domain.track.use_case.service.ITrackCalculator
import com.developers.sprintsync.domain.track.model.Segment
import com.developers.sprintsync.domain.track.model.Track
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

class TrackGenerator
    @Inject
    constructor(
        private val initializer: TrackInitializer,
        private val segmentService: SegmentService,
        private val trackCalculator: ITrackCalculator,
    ) {
        private var _trackFlow = MutableStateFlow(Track.INITIAL)
        val trackFlow = _trackFlow.asStateFlow()
        val track get() = trackFlow.value

        private val scope = CoroutineScope(Dispatchers.IO)

        init {
            observeSegmentFlow()
        }

        private fun observeSegmentFlow() = scope.launch { segmentService.data.collect { handleNewSegment(it) } }

        fun addTimedLocation(data: TimedLocation) = segmentService.addTimedLocation(data)

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
            val track = trackCalculator.updateTrackWithSegment(track, segment)
            updateTrackFlow(track)
        }

        private fun isTrackInitialized(): Boolean = track != Track.INITIAL

        private fun updateTrackFlow(track: Track) {
            _trackFlow.update { track }
        }
    }
