package com.developers.sprintsync.tracking.dataStorage.repository.track.useCase

import com.developers.sprintsync.tracking.session.model.track.Track
import com.developers.sprintsync.user.model.chart.chartData.util.time.TimeUtils
import com.developers.sprintsync.user.model.chart.chartData.util.time.TimestampBuilder
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class FilterTracksUseCase
    @Inject
    constructor(
        private val getTracksFlowUseCase: GetTracksFlowUseCase,
    ) {
        operator fun invoke(
            referenceTimestamp: Long,
            fromDayIndex: Int,
            toDayIndex: Int,
        ): Flow<List<Track>> =
            getTracksFlowUseCase.tracks.map { tracks ->
                val fromTimestamp = TimeUtils.shiftTimestampByDays(referenceTimestamp, fromDayIndex)
                val toTimestamp =
                    TimestampBuilder(referenceTimestamp).startOfDayTimestamp().shiftTimestampByDays(toDayIndex.inc()).build()
                tracks.filter { track ->
                    track.timestamp in fromTimestamp..toTimestamp
                }
            }
    }
