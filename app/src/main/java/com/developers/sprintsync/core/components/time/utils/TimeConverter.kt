package com.developers.sprintsync.core.components.time.utils

object TimeConverter {
    private const val MILLIS_IN_SECOND = 1000F
    private const val SECONDS_IN_MINUTE = 60F

    fun millisToMinutes(millis: Long): Float = millis / (MILLIS_IN_SECOND * SECONDS_IN_MINUTE)
}
