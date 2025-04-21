package com.developers.sprintsync.core.util.logger

import android.util.Log
import com.developers.sprintsync.domain.track.model.Segment

object SegmentLogger {

    fun log(segments: List<Segment>) {
        val timestamp = System.currentTimeMillis()

        val segmentDetails =
            segments.joinToString(separator = " | ") {
                when (it) {
                    is Segment.Active -> "ID:${it.id} [(${it.startLocation.latitude.value},${it.startLocation.longitude.value}) -> (${it.endLocation.latitude.value},${it.endLocation.longitude.value})]"
                    is Segment.Stationary -> "ID:${it.id} [(${it.location.latitude.value},${it.location.longitude.value})]"
                }
            }
        Log.d(TAG, "Segments: $segmentDetails")
    }

    private const val TAG = "My stack: SegmentLogger"
}