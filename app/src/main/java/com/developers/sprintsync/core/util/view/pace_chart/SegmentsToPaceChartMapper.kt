package com.developers.sprintsync.core.util.view.pace_chart

import com.developers.sprintsync.core.util.view.pace_chart.model.PaceChartData
import com.developers.sprintsync.domain.track.model.Segment
import com.github.mikephil.charting.data.Entry
import javax.inject.Inject

class SegmentsToPaceChartMapper @Inject constructor(){
    // TODO write approximating option
    fun map(segments: List<Segment>): PaceChartData {
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
