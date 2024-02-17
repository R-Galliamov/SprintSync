package com.developers.sprintsync.util.mapper.indicator

object TimeMapper {

    fun millisToPresentableTime(millis: Long): String {
        val seconds = millis / 1000 % 60
        val minutes = millis / (1000 * 60) % 60
        val hours = millis / (1000 * 60 * 60) % 24

        return String.format("%02d:%02d:%02d", hours, minutes, seconds)
    }
}