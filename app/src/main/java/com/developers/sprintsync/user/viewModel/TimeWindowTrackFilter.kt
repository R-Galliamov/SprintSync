package com.developers.sprintsync.user.viewModel

import com.developers.sprintsync.tracking.dataStorage.repository.track.useCase.FilterTracksUseCase
import com.developers.sprintsync.tracking.session.model.track.Track
import javax.inject.Inject

class TimeWindowTrackFilter
    @Inject
    constructor(
        private val filterTracksUseCase: FilterTracksUseCase,
    ) {
        suspend fun filterTracks(
            referenceTimestamp: Long,
            fromDayIndex: Int,
            toDayIndex: Int,
        ): List<Track> {
            val filteredTracks = mutableListOf<Track>()
            filterTracksUseCase(referenceTimestamp, fromDayIndex, toDayIndex).collect { tracks ->
                filteredTracks.addAll(tracks)
            }
            return filteredTracks
        }

        companion object {
            private const val TAG = "My Stack: WeeklyStatisticsLoader"
        }
    }
