package com.developers.sprintsync.core.util.log.logger

import android.util.Log
import com.developers.sprintsync.domain.track.model.Segment

object SegmentLogger {

    fun log(segments: List<Segment>) {
        val timestamp = System.currentTimeMillis()

        val segmentDetails =
            segments.joinToString(separator = " | ") {
                "ID:${it.id} [(${it.startLocation.latitude.value},${it.startLocation.longitude.value}) -> (${it.endLocation.latitude.value},${it.endLocation.longitude.value})]"
            }
        Log.d(TAG, "Segments: $segmentDetails")
    }

    private const val TAG = "My stack: SegmentLogger"
}