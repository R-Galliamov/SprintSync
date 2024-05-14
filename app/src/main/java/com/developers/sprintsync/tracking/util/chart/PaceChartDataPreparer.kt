package com.developers.sprintsync.tracking.util.chart

import android.util.Log
import com.developers.sprintsync.tracking.model.PaceChartData
import com.developers.sprintsync.tracking.model.Segment
import com.developers.sprintsync.tracking.model.Segments
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
                is Segment.ActiveSegment -> {
                    maxPace = maxOf(maxPace, segment.pace)
                    minPace = minOf(minPace, segment.pace)

                    currentEntriesList.add(getEntry(segment))
                }

                is Segment.InactiveSegment -> {
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

    private fun getEntry(segment: Segment.ActiveSegment): Entry {
        val timePoint = getTimePoint(segment)
        Log.d("My stack", "timePoint: $timePoint, pace: ${segment.pace}")
        return Entry(timePoint, segment.pace)
    }

    private fun getTimePoint(segment: Segment.ActiveSegment): Float {
        return ((segment.startTime + segment.endTime) / 2f)
    }
}
