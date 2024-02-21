package com.developers.sprintsync.model.tracking

import android.util.Log

//TODO optimise memory by changing types
data class TrackSegment(
    val startLocation: LocationModel? = null,
    val startTime: Long? = null,
    val endLocation: LocationModel? = null,
    val endTime: Long? = null,
    val distanceMeters: Int? = null,
    val durationMillis: Long? = null,
    val pace: Float? = null,
) {
    fun hasStartData(): Boolean = (startLocation != null && startTime != null)


    fun hasEndData(): Boolean = (endLocation != null && endTime != null)


    fun hasStartEndData(): Boolean = (hasStartData() && hasEndData()).also {
        Log.i(
            "My Stack",
            "HasStartEndData: $it")
    }

    fun withStartData(startLocation: LocationModel, startTime: Long) =
        this.copy(startLocation = startLocation, startTime = startTime)

    fun withEndData(endLocation: LocationModel, endTime: Long) =
        this.copy(endLocation = endLocation, endTime = endTime)
}
