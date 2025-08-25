package com.developers.sprintsync.domain.track.use_case.storage

import com.developers.sprintsync.core.util.log.AppLogger
import com.developers.sprintsync.data.track.repository.TrackRepository
import com.developers.sprintsync.domain.track.model.Track
import com.developers.sprintsync.domain.track.util.SegmentPaceSmoother
import com.developers.sprintsync.domain.track.validator.TrackValidator

/**
 * Use case for saving a track to the repository after validation and preparation.
 */
class SaveTrackUseCase(
    private val segmentPaceSmoother: SegmentPaceSmoother,
    private val trackRepository: TrackRepository,
    private val log: AppLogger,
) {

    /**
     * Saves a track after validating and preparing it.
     * @param track The [Track] to save.
     * @return The ID of the saved track.
     * @throws IllegalArgumentException if the track is invalid.
     */
    suspend operator fun invoke(track: Track): Int {
        try {
            log.d("Pace 1: ${track.segments.map { it.pace }}")
            val segments = segmentPaceSmoother.smooth(track.segments)
            log.d("Pace 2: ${segments.map { it.pace }}")
            val newTrack = track.copy(segments = segments, bestPace = segments.minOf { it.pace })
            TrackValidator.validateOrThrow(newTrack)
            val trackId = trackRepository.saveTrack(newTrack)
            log.i("Track saved: id=$trackId")
            return trackId
        } catch (e: Exception) {
            log.e(
                "Failed to save track: ${e.message}", e
            )
            throw e
        }
    }
}
