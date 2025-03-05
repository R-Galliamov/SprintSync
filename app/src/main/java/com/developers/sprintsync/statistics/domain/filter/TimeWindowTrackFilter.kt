package com.developers.sprintsync.statistics.domain.filter

import com.developers.sprintsync.core.util.timestamp.TimestampUtils
import com.developers.sprintsync.core.util.timestamp.TimestampBuilder
import com.developers.sprintsync.core.components.track.data.model.Track
import javax.inject.Inject

class TimeWindowTrackFilter
    @Inject
    constructor() {
        fun filterTracks(
            tracks: List<Track>,
            referenceTimestamp: Long,
            fromDayIndex: Int,
            toDayIndex: Int,
        ): List<Track> {
            val fromTimestamp = calculateFromTimestamp(referenceTimestamp, fromDayIndex)
            val toTimestamp = calculateToTimestamp(referenceTimestamp, toDayIndex)
            return tracks.filter { track ->
                track.timestamp in fromTimestamp..toTimestamp
            }
        }

        private fun calculateFromTimestamp(
            referenceTimestamp: Long,
            fromDayIndex: Int,
        ): Long = TimestampUtils.addDaysToTimestamp(referenceTimestamp, fromDayIndex)

        private fun calculateToTimestamp(
            referenceTimestamp: Long,
            toDayIndex: Int,
        ): Long =
            TimestampBuilder(referenceTimestamp)
                .startOfDayTimestamp()
                .shiftTimestampByDays(toDayIndex.inc())
                .build()

        companion object {
            private const val TAG = "My Stack: WeeklyStatisticsLoader"
        }
    }
