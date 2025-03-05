package com.developers.sprintsync.core.components.track.domain.use_case

import android.util.Log
import com.developers.sprintsync.core.util.timestamp.TimestampUtils
import com.developers.sprintsync.core.util.timestamp.TimestampBuilder
import com.developers.sprintsync.core.components.track.data.model.Track
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
        ): Flow<List<Track>> {
            Log.d("FilterTracksUseCase", "Start filtering tracks called")
            return getTracksFlowUseCase.tracks.map { tracks ->
                Log.d("FilterTracksUseCase", "Start filtering tracks")
                val fromTimestamp = TimestampUtils.addDaysToTimestamp(referenceTimestamp, fromDayIndex)
                val toTimestamp =
                    TimestampBuilder(referenceTimestamp)
                        .startOfDayTimestamp()
                        .shiftTimestampByDays(toDayIndex.inc())
                        .build()
                tracks.filter { track ->
                    track.timestamp in fromTimestamp..toTimestamp
                }
            }
        }
    }
