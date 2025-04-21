package com.developers.sprintsync.domain.track.use_case.storage

import com.developers.sprintsync.data.track.repository.TrackRepository
import com.developers.sprintsync.domain.track.model.Segment
import com.developers.sprintsync.domain.track.model.Track
import com.developers.sprintsync.domain.track.use_case.validator.TrackValidator
import javax.inject.Inject

class SaveTrackUseCase
    @Inject
    constructor(
        private val trackRepository: TrackRepository,
    ) {
        suspend operator fun invoke(track: Track): Int {
            TrackValidator.validateOrThrow(track)
            val preparedTrack = TrackPreparer.prepareForSave(track)
            return trackRepository.saveTrack(preparedTrack)
        }

        private object TrackPreparer {
            fun prepareForSave(track: Track): Track {
                val segments = StationarySegmentMerger.mergeStationarySegments(track.segments)
                return track.copy(segments = segments)
            }

            private object StationarySegmentMerger {
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
