package com.developers.sprintsync.util.mapper.indicator

object PaceMapper {

    fun paceToPresentablePace(pace: Float): String {
        return String.format("%.2f", pace)
    }
}