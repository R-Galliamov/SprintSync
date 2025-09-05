package com.developers.sprintsync.presentation.components.view.pace_chart

import com.developers.sprintsync.core.util.log.AppLogger
import com.developers.sprintsync.domain.track.model.LocationModel
import com.developers.sprintsync.presentation.components.view.pace_chart.model.PaceChartData
import com.developers.sprintsync.domain.track.model.Segment
import com.github.mikephil.charting.data.Entry
import javax.inject.Inject

class PaceChartDataCreator @Inject constructor(
    private val log: AppLogger,
) {
    fun create(segments: List<Segment>): PaceChartData {
        try {
            if (segments.isEmpty()) {
                log.d("No segments provided for pace chart")
                return PaceChartData.EMPTY
            }

            val data = mutableListOf<List<Entry>>()
            var current = mutableListOf<Entry>()
            var prevEnd: LocationModel? = null
            var prevEntry: Entry? = null

            var maxPace = Float.NEGATIVE_INFINITY
            var minPace = Float.POSITIVE_INFINITY

            for (s in segments) {
                s.pace?.let {
                    maxPace = maxOf(maxPace, it)
                    minPace = minOf(minPace, it)
                }

                val connected = prevEnd == null || s.startLocation == prevEnd
                if (!connected) {
                    if (current.isNotEmpty()) data += current.toList()
                    current = mutableListOf()
                    prevEntry = null
                }

                val entry = s.toEntry()
                if (entry != null) {
                    current += entry
                    prevEntry = entry
                } else if (prevEntry != null) {
                    current += prevEntry
                }

                prevEnd = s.endLocation
            }

            if (current.isNotEmpty()) data += current.toList()

            log.d(
                "Pace chart data created: segments=${segments.size}, entries=${data.sumOf { it.size }}, " +
                        "maxPace=$maxPace, minPace=$minPace"
            )
            return PaceChartData(data, maxPace, minPace)
        } catch (e: Exception) {
            log.e("Error creating pace chart data: ${e.message}", e)
            return PaceChartData.EMPTY
        }
    }

    private fun Segment.toEntry(): Entry? {
        try {
            require(startTime >= 0) { "Start time must be non-negative" }
            require(endTime > 0) { "End time must be greater than 0" }
            require(pace != null) { "Pace mustn't be null" }
            require(!pace!!.isNaN() && pace!!.isFinite()) { "Pace must be a valid number" }
            val centerTime = (startTime + endTime) / 2f
            return Entry(centerTime, pace!!)
        } catch (e: Exception) {
            log.e("Error converting segment to entry: ${e.message}", e)
            return null
        }
    }
}
