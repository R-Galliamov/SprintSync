package com.developers.sprintsync.manager.segment

import android.util.Log
import com.developers.sprintsync.model.tracking.LocationModel
import com.developers.sprintsync.model.tracking.TrackSegment
import javax.inject.Inject

class TrackSegmentManager @Inject constructor() {

    private val emptySegment = TrackSegment()
    private var currentSegment = emptySegment

    fun nextSegmentOrNull(
        location: LocationModel,
        timeMillis: Long
    ): TrackSegment? {
        val segment = getUpdatedSegment(location, timeMillis)
        setCurrentSegment(segment)
        return getSegmentOrNullIfHasNoStartEndData(segment)
    }

    private fun getUpdatedSegment(
        location: LocationModel,
        timeMillis: Long
    ): TrackSegment {
        val preparedSegment = getPreparedSegment()
        return getSegmentWithLocTimeData(preparedSegment, location, timeMillis)
    }

    private fun getPreparedSegment(): TrackSegment {
        val preparedSegment = if (currentSegment.hasStartEndData()) {
            val location = currentSegment.endLocation ?: error("End location cannot be null")
            val time = currentSegment.endTime ?: error("End time cannot be null")
            emptySegment.withStartData(location, time)
        } else {
            currentSegment
        }
        return preparedSegment
    }

    fun clear() {
        if (currentSegment != emptySegment) {
            currentSegment = emptySegment
        }
    }

    private fun getSegmentWithLocTimeData(
        segment: TrackSegment,
        location: LocationModel,
        timeMillis: Long
    ): TrackSegment {
        return if (!segment.hasStartData()) {
            segment.withStartData(location, timeMillis)
        } else {
            segment.withEndData(location, timeMillis)
        }
    }

    private fun setCurrentSegment(segment: TrackSegment) {
        currentSegment = segment
    }

    private fun getSegmentOrNullIfHasNoStartEndData(segment: TrackSegment): TrackSegment? =
        (if (segment.hasStartEndData()) segment else null).also {
            Log.i(
                "My Stack",
                "Final segment is: $it"
            )
        }
}
