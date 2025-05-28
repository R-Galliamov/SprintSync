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

    private val trackPreparer = TrackPreparer()

    /**
     * Saves a track after validating and preparing it.
     * @param track The [Track] to save.
     * @return The ID of the saved track.
     * @throws IllegalArgumentException if the track is invalid.
     */
    suspend operator fun invoke(track: Track): Int {
        try {
            TrackValidator.validateOrThrow(track)
            val preparedTrack = trackPreparer.prepareForSave(track)
            val trackId = trackRepository.saveTrack(preparedTrack)
            log.i("Track saved: id=$trackId")
            return trackId
        } catch (e: Exception) {
            log.e("Failed to save track: ${e.message}", e)
            throw e
        }
    }

    private inner class TrackPreparer {

        val segmentMerger = StationarySegmentMerger()

        fun prepareForSave(track: Track): Track {
            val segments = segmentMerger.mergeStationarySegments(track.segments)
            log.i("Prepared track with ${segments.size} segments after merging")
            return track.copy(segments = segments)
        }

        private inner class StationarySegmentMerger {
            fun mergeStationarySegments(segments: List<Segment>): List<Segment> {
                if (segments.isEmpty()) return emptyList()

                val result = mutableListOf<Segment>()
                var pendingStationary: Segment.Stationary? = null
                var nextId = 0L

                for (segment in segments) {
                    when (segment) {
                        is Segment.Active -> {
                            pendingStationary?.let {
                                result.add(it.copy(id = nextId++))
                            }
                            pendingStationary = null
                            result.add(segment.copy(id = nextId++))
                        }

                        is Segment.Stationary -> {
                            pendingStationary =
                                if (pendingStationary == null) {
                                    segment
                                } else {
                                    mergeStationary(pendingStationary, segment)
                                }
                        }
                    }
                }

                pendingStationary?.let { result.add(it.copy(id = nextId++)) }

                log.i("Merged ${segments.size} segments into ${result.size} segments")

                return result
            }

            private fun mergeStationary(
                first: Segment.Stationary,
                second: Segment.Stationary,
            ): Segment.Stationary {
                val mergedEndTime = first.endTime + second.endTime
                val mergedDuration = first.durationMillis + second.durationMillis
                return first.copy(endTime = mergedEndTime, durationMillis = mergedDuration)
            }
        }
    }
}
