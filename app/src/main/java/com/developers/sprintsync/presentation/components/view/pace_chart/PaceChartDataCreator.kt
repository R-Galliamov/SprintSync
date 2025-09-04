package com.developers.sprintsync.presentation.components.view.pace_chart

import com.developers.sprintsync.core.util.log.AppLogger
import com.developers.sprintsync.domain.track.model.LocationModel
import com.developers.sprintsync.presentation.components.view.pace_chart.model.PaceChartData
import com.developers.sprintsync.domain.track.model.Segment
import com.github.mikephil.charting.data.Entry
import javax.inject.Inject

/**
 * Creates [PaceChartData] from a list of track segments for pace chart visualization.
 */
class PaceChartDataCreator @Inject constructor(
    private val log: AppLogger,
) {
    /**
     * Converts segments into [PaceChartData], grouping active segments and tracking pace range.
     * @param segments List of [Segment] to process.
     * @return [PaceChartData] containing chart entries, max pace, and min pace.
     */
    fun create(segments: List<Segment>): PaceChartData {
        try {
            if (segments.isEmpty()) {
                log.d("No segments provided for pace chart")
                return PaceChartData.EMPTY
            }

            val data = mutableListOf<List<Entry>>()
            var current = mutableListOf<Entry>()
            var prevEnd: LocationModel? = null

            var maxPace = Float.NEGATIVE_INFINITY
            var minPace = Float.POSITIVE_INFINITY

            for (s in segments) {
                maxPace = maxOf(maxPace, s.pace)
                minPace = minOf(minPace, s.pace)

                val connected = prevEnd == null || s.startLocation == prevEnd
                if (!connected) {
                    if (current.isNotEmpty()) data += current.toList()
                    current = mutableListOf()
                }

                current += s.toEntry()
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


    // Converts a segment to a chart entry
    private fun Segment.toEntry(): Entry {
        try {
            require(startTime >= 0) { "Start time must be non-negative" }
            require(endTime > 0) { "End time must be greater than 0" }
            require(!pace.isNaN() && pace.isFinite()) { "Pace must be a valid number" }
            val centerTime = (startTime + endTime) / 2f
            return Entry(centerTime, pace)
        } catch (e: Exception) {
            log.e("Error converting segment to entry: ${e.message}", e)
            return EMPTY_ENTRY
        }
    }

    private companion object {
        private val EMPTY_ENTRY = Entry(Float.NaN, Float.NaN)
    }
}
