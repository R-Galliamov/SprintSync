package com.developers.sprintsync.domain.track.use_case.storage

import com.developers.sprintsync.core.util.log.AppLogger
import com.developers.sprintsync.data.track.repository.TrackRepository
import com.developers.sprintsync.domain.track.model.Segment
import com.developers.sprintsync.domain.track.model.Track
import com.developers.sprintsync.domain.track.use_case.validator.TrackValidator
import javax.inject.Inject

/**
 * Use case for saving a track to the repository after validation and preparation.
 */
class SaveTrackUseCase
@Inject
constructor(
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
            TrackValidator.validateOrThrow(track)
            val trackId = trackRepository.saveTrack(track)
            log.i("Track saved: id=$trackId")
            return trackId
        } catch (e: Exception) {
            log.e("Failed to save track: ${e.message}", e)
            throw e
        }
    }
}
