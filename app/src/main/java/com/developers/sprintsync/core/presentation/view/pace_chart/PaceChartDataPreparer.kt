package com.developers.sprintsync.core.presentation.view.pace_chart

import com.developers.sprintsync.core.presentation.view.pace_chart.model.PaceChartData
import com.developers.sprintsync.core.components.track.data.model.Segment
import com.developers.sprintsync.core.components.track.data.model.Segments
import com.github.mikephil.charting.data.Entry

class PaceChartDataPreparer {
    // TODO write approximating option
    fun getChartData(segments: Segments): PaceChartData {
        val data = mutableListOf<List<Entry>>()
        var currentEntriesList = mutableListOf<Entry>()
        var maxPace = Float.MIN_VALUE
        var minPace = Float.MAX_VALUE

        segments.forEach { segment ->
            when (segment) {
                is Segment.Active -> {
                    maxPace = maxOf(maxPace, segment.pace)
                    minPace = minOf(minPace, segment.pace)

                    currentEntriesList.add(getEntry(segment))
                }

                is Segment.Stationary -> {
                    if (currentEntriesList.isNotEmpty()) {
                        data.add(currentEntriesList.toList())
                        currentEntriesList = mutableListOf()
                    }
                }
            }
        }

        if (currentEntriesList.isNotEmpty()) {
            data.add(currentEntriesList.toList())
        }

        return PaceChartData(data, maxPace, minPace)
    }

    private fun getEntry(segment: Segment.Active): Entry {
        val timePoint = getTimePoint(segment)
        return Entry(timePoint, segment.pace)
    }

    private fun getTimePoint(segment: Segment.Active): Float = ((segment.startTime + segment.endTime) / 2f)
}
