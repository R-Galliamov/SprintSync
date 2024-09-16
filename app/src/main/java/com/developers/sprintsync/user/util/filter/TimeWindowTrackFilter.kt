package com.developers.sprintsync.user.util.filter

import com.developers.sprintsync.tracking.session.model.track.Track
import com.developers.sprintsync.user.model.chart.chartData.util.time.TimeUtils
import com.developers.sprintsync.user.model.chart.chartData.util.time.TimestampBuilder
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
        ): Long = TimeUtils.shiftTimestampByDays(referenceTimestamp, fromDayIndex)

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
