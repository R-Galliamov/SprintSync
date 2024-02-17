package com.developers.sprintsync.util.mapper.indicator

object DistanceMapper {

    //TODO based on setting mapper can decide what function to use to convert values
    fun metersToPresentableDistance(distanceInMeters: Int): String {
        val kilometers = distanceInMeters / 1000.0
        return String.format("%.2f", kilometers)
    }
}