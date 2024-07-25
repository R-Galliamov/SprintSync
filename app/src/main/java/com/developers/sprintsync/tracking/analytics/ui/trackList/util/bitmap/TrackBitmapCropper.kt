package com.developers.sprintsync.tracking.analytics.ui.trackList.util.bitmap

import android.graphics.Bitmap
import android.util.Log
import com.developers.sprintsync.tracking.session.model.track.Segment
import com.developers.sprintsync.tracking.session.model.track.Segments
import com.developers.sprintsync.tracking.session.model.track.toLatLng
import com.google.android.gms.maps.Projection
import com.google.android.gms.maps.model.LatLng

class TrackBitmapCropper {
    companion object {
        fun crop(
            bitmap: Bitmap,
            projection: Projection,
            segments: Segments,
            padding: Int,
        ): Bitmap {
            val coordinates = mutableListOf<LatLng>()
            segments.forEach { segment ->
                if (segment is Segment.ActiveSegment) {
                    coordinates.add(segment.startLocation.toLatLng())
                    coordinates.add(segment.endLocation.toLatLng())
                }
            }

            var top = Int.MAX_VALUE
            var bottom = 0
            var left = Int.MAX_VALUE
            var right = 0

            coordinates.forEach { latLng ->
                val point = projection.toScreenLocation(latLng)
                left = minOf(left, point.x)
                right = maxOf(right, point.x)
                top = minOf(top, point.y)
                bottom = maxOf(bottom, point.y)
            }

            Log.d("MyStack", "left: $left, top: $top, right: $right, bottom: $bottom")

            left = maxOf(0, left - padding)
            top = maxOf(0, top - padding)
            right = minOf(bitmap.width, right + padding)
            bottom = minOf(bitmap.height, bottom + padding)
            Log.d("MyStack", "left: $left, top: $top, right: $right, bottom: $bottom")
            return Bitmap.createBitmap(bitmap, left, top, right - left, bottom - top)
        }
    }
}
