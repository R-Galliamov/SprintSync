package com.developers.sprintsync.core.util.test

import java.util.Calendar

object TestTimestamps {
    val currentTimestamp = System.currentTimeMillis()

    val mondayTimestamp1 =
        Calendar
            .getInstance()
            .apply {
                set(2024, Calendar.AUGUST, 5) // Assume this is a Monday
            }.timeInMillis

    val tuesdayTimestamp =
        Calendar
            .getInstance()
            .apply {
                set(2024, Calendar.AUGUST, 6)
            }.timeInMillis

    val wednesdayTimestamp1 =
        Calendar
            .getInstance()
            .apply {
                set(2024, Calendar.AUGUST, 7)
            }.timeInMillis

    val thursdayTimestamp1 =
        Calendar
            .getInstance()
            .apply {
                set(2024, Calendar.AUGUST, 8)
            }.timeInMillis

    val fridayTimestamp1 =
        Calendar
            .getInstance()
            .apply {
                set(2024, Calendar.AUGUST, 9)
            }.timeInMillis

    val saturdayTimestamp1 =
        Calendar
            .getInstance()
            .apply {
                set(2024, Calendar.AUGUST, 10)
            }.timeInMillis

    val sundayTimestamp1 =
        Calendar
            .getInstance()
            .apply {
                set(2024, Calendar.AUGUST, 11)
            }.timeInMillis

    val mondayTimestamp2 =
        Calendar
            .getInstance()
            .apply {
                set(2024, Calendar.AUGUST, 12) // Another Monday
            }.timeInMillis

    val mondayTimestamp3 =
        Calendar
            .getInstance()
            .apply {
                set(2024, Calendar.AUGUST, 19) // Another Monday
            }.timeInMillis
}
