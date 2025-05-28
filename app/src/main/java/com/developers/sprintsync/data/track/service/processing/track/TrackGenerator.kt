package com.developers.sprintsync.data.track.service.processing.track

import com.developers.sprintsync.core.util.log.AppLogger
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

// Generates and manages track data from segments
class TrackGenerator
@Inject
constructor(
    private val initializer: TrackInitializer,
    private val segmentService: SegmentService,
    private val trackCalculator: ITrackCalculator,
    private val log: AppLogger,
) {
    private var _trackFlow = MutableStateFlow(Track.INITIAL)
    val trackFlow = _trackFlow.asStateFlow()
    val track get() = trackFlow.value

    private val scope = CoroutineScope(Dispatchers.IO)

    init {
        observeSegmentFlow()
        log.i("TrackGenerator initialized")
    }

    // Observes segment data flow and processes new segments
    private fun observeSegmentFlow() = scope.launch {
        segmentService.data.collect {
            handleNewSegment(it)
            log.i("New segment received: $it")
        }
    }

    // Adds new timed location to segment service
    fun addTimedLocation(data: TimedLocation) = segmentService.addTimedLocation(data)

    // Resets last timed location in segment service,
    // for i.e. when service is paused and there is no need to create segment
    fun resetLastTimedLocation() = segmentService.resetData()

    // Handles new segment by initializing or updating current track
    private fun handleNewSegment(segment: Segment) {
        if (isTrackInitialized().not()) {
            initializeTrack(segment)
        } else {
            addSegmentToTrack(segment)
        }
    }

    // Initializes track with the first segment
    private fun initializeTrack(segment: Segment) {
        val track = initializer.initializeTrack(segment)
        updateTrackFlow(track)
        log.i("Track initialized with segment: $segment")
    }

    // Adds a new segment to the existing track
    private fun addSegmentToTrack(segment: Segment) {
        val track = trackCalculator.updateTrackWithSegment(track, segment)
        updateTrackFlow(track)
        log.i("Track updated with new segment: $segment")
    }

    // Checks if track is initialized
    private fun isTrackInitialized(): Boolean = track != Track.INITIAL

    // Updates track flow with new track data
    private fun updateTrackFlow(track: Track) {
        _trackFlow.update { track }
        log.i("Track flow updated: $track")
    }
}
