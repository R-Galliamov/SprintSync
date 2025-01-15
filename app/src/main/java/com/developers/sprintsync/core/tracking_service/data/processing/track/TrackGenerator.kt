package com.developers.sprintsync.core.tracking_service.data.processing.track

import com.developers.sprintsync.core.components.track.data.model.Segment
import com.developers.sprintsync.core.components.track.data.model.Track
import com.developers.sprintsync.core.tracking_service.data.model.location.LocationModel
import com.developers.sprintsync.core.tracking_service.data.processing.segment.SegmentGenerator
import com.developers.sprintsync.core.tracking_service.data.processing.segment.SegmentType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

class TrackGenerator
    @Inject
    constructor(
        private val initializer: TrackInitializer,
        private val segmentGenerator: SegmentGenerator,
        private val trackUpdater: TrackUpdater,
    ) {
        private var _trackFlow = MutableStateFlow(Track.EMPTY_TRACK_DATA)
        val trackFlow = _trackFlow.asStateFlow()
        val track get() = trackFlow.value

        private val scope = CoroutineScope(Dispatchers.IO)

        init {
            observeSegmentFlow()
        }

        private fun observeSegmentFlow() {
            scope.launch {
                segmentGenerator.data.collect { segment ->
                    handleNewSegment(segment)
                }
            }
        }

        fun addDataPoint(
            type: SegmentType,
            location: LocationModel,
            timeMillis: Long,
        ) {
            segmentGenerator.addPoint(type, location, timeMillis)
        }

        fun clearLastDataPoint() {
            segmentGenerator.resetData()
        }

        fun reset() {
            _trackFlow.update { Track.EMPTY_TRACK_DATA }
            clearLastDataPoint()
        }

        private fun handleNewSegment(segment: Segment) {
            if (isTrackInitialized().not()) {
                initializeTrack(segment)
            } else {
                updateTrack(segment)
            }
        }

        private fun initializeTrack(segment: Segment) {
            val track = initializer.initializeTrack(segment)
            updateTrackFlow(track)
        }

        private fun updateTrack(segment: Segment) {
            val updatedTrack: Track =
                when (segment) {
                    is Segment.ActiveSegment -> {
                        trackUpdater.getTrackUpdatedWithSegment(track, segment)
                    }

                    is Segment.InactiveSegment -> {
                        trackUpdater.getTrackUpdatedWithSegment(track, segment)
                    }
                }
            updateTrackFlow(updatedTrack)
        }

        private fun isTrackInitialized(): Boolean = track != Track.EMPTY_TRACK_DATA

        private fun updateTrackFlow(track: Track) {
            _trackFlow.update { track }
        }
    }
